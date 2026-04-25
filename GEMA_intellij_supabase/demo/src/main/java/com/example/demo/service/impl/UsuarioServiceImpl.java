package com.example.demo.service.impl;

import com.example.demo.dto.UsuarioUpdateDto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> getAll() {
        // Obtenemos el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return java.util.Collections.emptyList();
        }

        Object principal = auth.getPrincipal();
        Usuario currentUser = null;

        if (principal instanceof Usuario) {
            currentUser = (Usuario) principal;
        } else {
            // Si el principal es un String o UserDetails, buscamos el objeto completo
            currentUser = usuarioRepository.findByUser(auth.getName()).orElse(null);
        }

        if (currentUser != null && currentUser.getIdNegocio() != null) {
            // Solo devolvemos a los miembros de la misma empresa
            return usuarioRepository.findByIdNegocio(currentUser.getIdNegocio());
        }

        return java.util.Collections.emptyList();
    }

    @Override
    public Usuario getById(String id) { // <-- Corregido a String
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Asegúrate de asignar el idRol
        // (Probablemente quieras recibirlo desde el DTO)
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(String id) { // <-- Corregido a String
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario update(String id, Usuario usuario) {
        // 1. Obtener quién está intentando modificar (usuario autenticado)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida");
        }

        Object principal = auth.getPrincipal();
        Usuario modificador = (principal instanceof Usuario) ? (Usuario) principal : usuarioRepository.findByUser(auth.getName()).orElse(null);

        if (modificador == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Modificador no encontrado");
        }

        // 2. Buscar el usuario objetivo
        Usuario objetivo = usuarioRepository.findById(id).orElse(null);
        if (objetivo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario objetivo no encontrado");
        }

        // 3. REGLAS DE JERARQUÍA Y NEGOCIO
        // Deben pertenecer al mismo negocio
        if (modificador.getIdNegocio() == null || !modificador.getIdNegocio().equals(objetivo.getIdNegocio())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Error de Negocio: No puedes modificar usuarios de otra empresa.");
        }

        // Admin (1) -> Puede todo dentro de su negocio
        // Supervisor (2) -> Solo puede modificar Operarios (3). No puede modificar Admins (1) ni otros Supervisors (2).
        if (modificador.getIdRol() == 2 && objetivo.getIdRol() != 3) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Jerarquia insuficiente: Un Supervisor solo puede modificar Operarios.");
        }

        // Operario (3) -> No puede modificar nada
        if (modificador.getIdRol() == 3) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Acceso denegado: Los Operarios no pueden modificar usuarios.");
        }

        // 4. Actualizar campos
        objetivo.setUser(usuario.getUser());
        objetivo.setNombre(usuario.getNombre());
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            objetivo.setPassword(usuario.getPassword());
        }
        objetivo.setIdRol(usuario.getIdRol());

        return usuarioRepository.save(objetivo);
    }

    @Override
    public Usuario actualizarPerfil(String id, UsuarioUpdateDto dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo actualizamos los campos permitidos
        u.setNombre(dto.getNombre());
        u.setDireccion(dto.getDireccion());
        u.setTelefono(dto.getTelefono());
        if (dto.getImagenUrl() != null) {
            u.setImagenUrl(dto.getImagenUrl());
        }

        return usuarioRepository.save(u);
    }

    @Override
    public void registersTokenFCM(String id, String token) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            u.setFcmToken(token);
            usuarioRepository.save(u);
        }
    }

    @Override
    public Usuario updateNotificationSettings(String id, Usuario settings) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setNotifyLowStock(settings.getNotifyLowStock());
        u.setNotifyNewOrders(settings.getNotifyNewOrders());
        u.setNotifyInventoryChanges(settings.getNotifyInventoryChanges());
        return usuarioRepository.save(u);
    }
}