package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Esta será la base para login y registro
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- LOGIN (Reemplaza a login.php) ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String correo, @RequestParam String contrasena) {
        Map<String, Object> response = new HashMap<>();

        // Buscamos por correo (que en tu modelo se llama 'user')
        // Nota: Necesitas agregar findByUser en tu repositorio o usar un ejemplo manual aquí
        // Asumiremos que mapeaste 'user' a la columna 'correo'
        Optional<Usuario> userOpt = usuarioRepository.findAll().stream()
                .filter(u -> u.getUser().equals(correo))
                .findFirst();

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(contrasena)) {
            response.put("success", true);
            response.put("usuario", userOpt.get()); // Devuelve el objeto usuario
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(response);
        }
    }

    // --- REGISTRO (Reemplaza a register.php) ---
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String direccion,
            @RequestParam String telefono) {

        Map<String, Object> response = new HashMap<>();

        // Verificar si existe
        boolean existe = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getUser().equals(correo));

        if (existe) {
            response.put("success", false);
            response.put("message", "El correo ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear nuevo usuario
        Usuario nuevo = Usuario.builder()
                .nombre(nombre)
                .user(correo) // Mapeado a columna 'correo'
                .password(contrasena)
                .direccion(direccion)
                .telefono(telefono)
                .idRol(1) // Rol por defecto
                .build();

        usuarioRepository.save(nuevo);

        response.put("success", true);
        response.put("message", "Usuario registrado con éxito");
        return ResponseEntity.ok(response);
    }
}