package com.example.demo.controller;

import com.example.demo.dto.GoogleLoginDto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Esta será la base para login y registro
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordResetService resetService;

    // --- LOGIN (Reemplaza a login.php) ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String correo, @RequestParam String contrasena) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> userOpt = usuarioRepository.findByUserAndActivoTrue(correo);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(contrasena)) {
            Usuario user = userOpt.get();
            String token = jwtUtil.generateToken(user.getUser(), user.getId(), user.getIdRol());

            response.put("success", true);
            response.put("usuario", user);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(response);
        }
    }

    // --- REGISTRO (Reemplaza a register.php) ---
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String direccion,
            @RequestParam String telefono) {

        Map<String, Object> response = new HashMap<>();

        // Verificar si existe
        Optional<Usuario> userOpt = usuarioRepository.findByUserAndActivoTrue(correo);

        if (userOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "El correo ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        // Ya no creamos Negocio por defecto aquí. Esto se hará en el onboarding.
        
        // Crear nuevo usuario SIN negocio asigando inicialmente
        Usuario nuevo = Usuario.builder()
                .nombre(nombre)
                .user(correo) // Mapeado a columna 'correo'
                .password(contrasena)
                .direccion(direccion)
                .telefono(telefono)
                .idRol(1) // Rol por defecto (Dueño) si crean uno
                .idNegocio(null) // Debe quedar null obligatoriamente para saltar el Onboarding
                .build();

        usuarioRepository.save(nuevo);

        // Generar token para permitir login inmediato
        String token = jwtUtil.generateToken(nuevo.getUser(), nuevo.getId(), nuevo.getIdRol());

        response.put("success", true);
        response.put("message", "Usuario registrado con éxito");
        response.put("usuario", nuevo);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        resetService.requestPasswordReset(email);
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Se ha enviado un código a tu correo");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        resetService.resetPassword(email, code, newPassword);
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Contraseña restablecida con éxito");
        return ResponseEntity.ok(resp);
    }

    // --- LOGIN CON GOOGLE ---
    @PostMapping("/google")
    @Transactional
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleLoginDto dto) {
        Map<String, Object> response = new HashMap<>();

        // 1. Buscar si ya existe
        Optional<Usuario> userOpt = usuarioRepository.findByUserAndActivoTrue(dto.getCorreo());

        Usuario usuarioFinal;

        if (userOpt.isPresent()) {
            // YA EXISTE: Iniciamos sesión
            usuarioFinal = userOpt.get();
        } else {
            // NO EXISTE: Lo registramos automáticamente SIN negocio asignado
            // Esto obliga al usuario a pasar por el Onboarding (Crear o Unirse)
            Usuario nuevo = new Usuario();
            nuevo.setUser(dto.getCorreo());
            nuevo.setNombre(dto.getNombre());
            nuevo.setPassword(""); // Sin contraseña porque entra con Google
            nuevo.setDireccion("Pendiente");
            nuevo.setTelefono("Pendiente");
            nuevo.setIdRol(1); // Rol inicial 1 (Administrador potencial)
            nuevo.setIdNegocio(null); // Obligatorio para ruteo a Onboarding

            usuarioFinal = usuarioRepository.save(nuevo);
        }

        String token = jwtUtil.generateToken(usuarioFinal.getUser(), usuarioFinal.getId(), usuarioFinal.getIdRol());

        // Devolvemos éxito
        response.put("success", true);
        response.put("usuario", usuarioFinal);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}