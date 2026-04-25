package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenancyService {
    
    private final UsuarioRepository usuarioRepository;

    /**
     * Resuelve el ID del Negocio para aislar los datos.
     * Si el usuario no tiene negocio (datos antiguos), devuelve su propio ID.
     */
    public String resolveNegocioId(String userId) {
        if (userId == null) return null;
        return usuarioRepository.findById(userId)
                .map(u -> (u.getIdNegocio() != null && !u.getIdNegocio().isEmpty()) ? u.getIdNegocio() : userId)
                .orElse(userId);
    }
}
