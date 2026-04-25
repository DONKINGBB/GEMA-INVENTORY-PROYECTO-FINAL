package com.example.demo.service.impl;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
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

    // --- ESTE ES EL MÉTODO CORREGIDO ---
    @Override
    public Usuario update(String id, Usuario usuario) { // <-- Corregido a String
        // 1. Busca el usuario existente
        Usuario aux = usuarioRepository.findById(id).orElse(null);

        if (aux != null) {
            // 2. Actualiza los campos del objeto "aux" (de la BD)
            //    con los valores del objeto "usuario" (que llega)
            aux.setUser(usuario.getUser());
            aux.setNombre(usuario.getNombre());
            aux.setPassword(usuario.getPassword());
            aux.setIdRol(usuario.getIdRol()); // Actualiza el rol también

            // 3. Guarda el objeto "aux" actualizado
            return usuarioRepository.save(aux);
        }
        return null; // O lanza una excepción
    }
}