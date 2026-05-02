package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// El segundo tipo genérico debe ser String, no Integer
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByCorreoAndActivoTrue(String correo);
    java.util.List<Usuario> findByIdNegocioAndActivoTrue(String idNegocio);
}