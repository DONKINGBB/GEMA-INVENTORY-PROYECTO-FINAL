package com.example.demo.controller;

import com.example.demo.model.Negocio;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.example.demo.repository.UsuarioNegocioRepository usuarioNegocioRepository;

    @GetMapping("/mis-negocios")
    public ResponseEntity<List<Map<String, String>>> getMisNegocios() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) return ResponseEntity.status(401).build();
        Usuario currentUser = (Usuario) principal;

        java.util.List<com.example.demo.model.UsuarioNegocio> relaciones = 
            usuarioNegocioRepository.findById_IdUsuario(currentUser.getId());
        
        java.util.List<Map<String, String>> result = new java.util.ArrayList<>();
        for (com.example.demo.model.UsuarioNegocio rel : relaciones) {
            Map<String, String> map = new HashMap<>();
            map.put("id", rel.getNegocio().getIdNegocio());
            map.put("nombre", rel.getNegocio().getNombre());
            map.put("idRol", String.valueOf(rel.getIdRol()));
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/switch/{idNegocio}")
    @Transactional
    public ResponseEntity<Map<String, Object>> switchNegocio(@PathVariable String idNegocio) {
        Map<String, Object> response = new HashMap<>();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) return ResponseEntity.status(401).build();
        Usuario currentUser = (Usuario) principal;

        // 1. Verificar que el usuario pertenezca a ese negocio
        java.util.Optional<com.example.demo.model.UsuarioNegocio> relOpt = 
            usuarioNegocioRepository.findById_IdUsuarioAndId_IdNegocio(currentUser.getId(), idNegocio);

        if (relOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "No tienes acceso a este negocio");
            return ResponseEntity.status(403).body(response);
        }

        // 2. Actualizar el negocio activo en el Usuario
        currentUser.setIdNegocio(idNegocio);
        currentUser.setIdRol(relOpt.get().getIdRol());
        usuarioRepository.save(currentUser);

        response.put("success", true);
        response.put("usuario", currentUser);
        response.put("message", "Cambiado al negocio: " + relOpt.get().getNegocio().getNombre());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mi-negocio")
    public ResponseEntity<Negocio> getMiNegocio() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            return ResponseEntity.status(401).build();
        }
        Usuario currentUser = (Usuario) principal;

        // Si el usuario no tiene negocio asignado (Onboarding), retornar 404
        if (currentUser.getIdNegocio() == null) {
            return ResponseEntity.notFound().build();
        }

        Negocio negocio = negocioRepository.findById(currentUser.getIdNegocio()).orElse(null);
        if (negocio == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(negocio);
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Map<String, Object>> createNegocio(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        String nombre = body.get("nombre");

        if (nombre == null || nombre.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Nombre del negocio es requerido");
            return ResponseEntity.badRequest().body(response);
        }

        if (negocioRepository.existsByNombre(nombre)) {
            response.put("success", false);
            response.put("message", "El nombre '" + nombre + "' ya brilla en otro negocio de nuestra red. ¡Elige uno único!");
            return ResponseEntity.status(409).body(response);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            return ResponseEntity.status(401).build();
        }
        Usuario currentUser = (Usuario) principal;

        Negocio nuevo = Negocio.builder()
                .nombre(nombre)
                .propietarioId(currentUser.getId()) // ¡ASIGNAMOS EL DUEÑO!
                .codigoInvitacion(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
        Negocio negocioGuardado = negocioRepository.save(nuevo);

        currentUser.setIdNegocio(negocioGuardado.getIdNegocio());
        currentUser.setIdRol(1); // 1 = PROPIETARIO
        Usuario managedUser = usuarioRepository.save(currentUser);

        // --- REGISTRAMOS LA RELACIÓN MULTI-NEGOCIO ---
        com.example.demo.model.UsuarioNegocio rel = com.example.demo.model.UsuarioNegocio.builder()
                .id(new com.example.demo.model.UsuarioNegocio.UsuarioNegocioId(managedUser.getId(), negocioGuardado.getIdNegocio()))
                .usuario(managedUser)
                .negocio(negocioGuardado)
                .idRol(1) // 1 = PROPIETARIO
                .build();
        usuarioNegocioRepository.save(rel);

        response.put("success", true);
        response.put("negocio", negocioGuardado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Negocio> updateNegocioName(@PathVariable String id, @RequestBody Map<String, String> body) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) return ResponseEntity.status(401).build();
        
        Usuario currentUser = (Usuario) principal;
        if (currentUser.getIdRol() != 1) return ResponseEntity.status(403).build(); // Solo Admin

        Negocio negocio = negocioRepository.findById(id).orElse(null);
        if (negocio == null) return ResponseEntity.notFound().build();

        String nuevoNombre = body.get("nombre");
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            if (!nuevoNombre.equals(negocio.getNombre()) && negocioRepository.existsByNombre(nuevoNombre)) {
                return ResponseEntity.status(409).build(); // O podrías retornar un Map con mensaje
            }
            negocio.setNombre(nuevoNombre);
            negocioRepository.save(negocio);
        }

        return ResponseEntity.ok(negocio);
    }

    @PostMapping("/join")
    @Transactional
    public ResponseEntity<Map<String, Object>> joinNegocio(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        String codigo = body.get("codigoInvitacion");

        if (codigo == null || codigo.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Código de invitación requerido");
            return ResponseEntity.badRequest().body(response);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }
        Usuario currentUser = (Usuario) principal;

        Optional<Negocio> negocioOpt = negocioRepository.findByCodigoInvitacion(codigo.toUpperCase());
        if (negocioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Código de invitación inválido");
            return ResponseEntity.badRequest().body(response);
        }

        // Unirse al negocio como Almacenista (rol 6 - nivel base)
        currentUser.setIdNegocio(negocioOpt.get().getIdNegocio());
        currentUser.setIdRol(6); 
        Usuario managedUser = usuarioRepository.save(currentUser);

        // --- REGISTRAMOS LA RELACIÓN MULTI-NEGOCIO ---
        com.example.demo.model.UsuarioNegocio rel = com.example.demo.model.UsuarioNegocio.builder()
                .id(new com.example.demo.model.UsuarioNegocio.UsuarioNegocioId(managedUser.getId(), negocioOpt.get().getIdNegocio()))
                .usuario(managedUser)
                .negocio(negocioOpt.get())
                .idRol(6)
                .build();
        usuarioNegocioRepository.save(rel);

        response.put("success", true);
        response.put("message", "Te has unido al negocio exitosamente");
        response.put("negocio", negocioOpt.get());
        
        return ResponseEntity.ok(response);
    }
}
