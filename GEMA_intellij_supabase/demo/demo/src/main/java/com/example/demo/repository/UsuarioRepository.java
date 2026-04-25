package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// El segundo tipo genérico debe ser String, no Integer
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}