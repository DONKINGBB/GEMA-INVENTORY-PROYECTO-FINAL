package com.example.demo.service;

import com.example.demo.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll();
    Usuario getById(String id); // <-- Corregido
    Usuario save(Usuario usuario);
    void delete(String id); // <-- Corregido
    Usuario update(String id, Usuario usuario); // <-- Corregido
}