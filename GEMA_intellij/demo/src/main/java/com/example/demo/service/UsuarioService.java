package com.example.demo.service;

import com.example.demo.dto.UsuarioUpdateDto;
import com.example.demo.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll();

    Usuario getById(String id); // <-- Corregido

    Usuario save(Usuario usuario);

    void delete(String id); // <-- Corregido

    Usuario update(String id, Usuario usuario); // <-- Corregido

    Usuario actualizarPerfil(String id, UsuarioUpdateDto dto);

    void registersTokenFCM(String id, String token);

    Usuario updateNotificationSettings(String id, Usuario settings);

    void changePassword(String id, String oldPassword, String newPassword);
}