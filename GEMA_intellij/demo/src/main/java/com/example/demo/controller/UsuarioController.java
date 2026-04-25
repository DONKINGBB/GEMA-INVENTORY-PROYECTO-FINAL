package com.example.demo.controller;
 
import com.example.demo.dto.FcmTokenDto;
import com.example.demo.dto.PasswordUpdateDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.dto.UsuarioUpdateDto;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {
 
    @Autowired
    private final UsuarioService usuarioService;
 
    // Método privado para convertir Entidad a DTO
    private UsuarioDto convertirADto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .user(u.getUser())
                .nombre(u.getNombre())
                .idRol(u.getIdRol())
                .fcmToken(u.getFcmToken())
                .notifyLowStock(u.getNotifyLowStock())
                .notifyNewOrders(u.getNotifyNewOrders())
                .notifyInventoryChanges(u.getNotifyInventoryChanges())
                .imagenUrl(u.getImagenUrl())
                .build();
    }
 
    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(
            @RequestParam(name = "user", defaultValue = "", required = false) String user) {
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
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
 
    // La ruta final será: /api/usuarios/{id}/perfil
    @PutMapping("/{id}/perfil")
    public ResponseEntity<Usuario> updateProfile(@PathVariable String id, @RequestBody UsuarioUpdateDto dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
    }
 
    @PostMapping("/{id}/fcm-token")
    public ResponseEntity<Void> registerFcmToken(@PathVariable String id, @RequestBody FcmTokenDto dto) {
        usuarioService.registersTokenFCM(id, dto.getToken());
        return ResponseEntity.ok().build();
    }
 
    @PutMapping("/{id}/notificaciones")
    public ResponseEntity<UsuarioDto> updateNotifications(@PathVariable String id, @RequestBody UsuarioDto dto) {
        Usuario settings = Usuario.builder()
                .notifyLowStock(dto.getNotifyLowStock())
                .notifyNewOrders(dto.getNotifyNewOrders())
                .notifyInventoryChanges(dto.getNotifyInventoryChanges())
                .build();
        Usuario actualizado = usuarioService.updateNotificationSettings(id, settings);
        return ResponseEntity.ok(convertirADto(actualizado));
    }
 
    @GetMapping("/{id}/preferencias")
    public ResponseEntity<UsuarioDto> getPreferences(@PathVariable String id) {
        Usuario u = usuarioService.getById(id);
        if (u == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(convertirADto(u));
    }
 
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable String id, @RequestBody PasswordUpdateDto dto) {
        usuarioService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
        Map<String, String> resp = new java.util.HashMap<>();
        resp.put("message", "Contraseña actualizada con éxito");
        return ResponseEntity.ok(resp);
    }
}
