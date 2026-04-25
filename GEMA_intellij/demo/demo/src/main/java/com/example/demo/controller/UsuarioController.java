package com.example.demo.controller;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Método privado para convertir Entidad a DTO
    private UsuarioDto convertirADto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .user(u.getUser())
                .nombre(u.getNombre())
                .idRol(u.getIdRol())
                .build(); // ⚠️ password eliminado por seguridad
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(@RequestParam(name = "user", defaultValue = "", required = false) String user) {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UsuarioDto> dtos = usuarios.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        if (user != null && !user.isEmpty()) {
            dtos = dtos.stream()
                    .filter(u -> u.getUser().equals(user))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable String id) {
        Usuario u = usuarioService.getById(id);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(u));
    }

    @PostMapping("/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        Usuario u = Usuario.builder()
                .user(usuarioDto.getUser())
                .nombre(usuarioDto.getNombre())
                .password(usuarioDto.getPassword()) // ⚠️ solo se usa internamente
                .idRol(usuarioDto.getIdRol())
                .build();

        Usuario guardado = usuarioService.save(u);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable String id, @RequestBody UsuarioDto usuarioDto) {
        Usuario datosParaActualizar = Usuario.builder()
                .user(usuarioDto.getUser())
                .nombre(usuarioDto.getNombre())
                .password(usuarioDto.getPassword())
                .idRol(usuarioDto.getIdRol())
                .build();

        Usuario actualizado = usuarioService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
