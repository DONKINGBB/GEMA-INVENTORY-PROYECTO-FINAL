package com.example.demo.repository;

import com.example.demo.model.UsuarioNegocio;
import com.example.demo.model.UsuarioNegocio.UsuarioNegocioId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioNegocioRepository extends JpaRepository<UsuarioNegocio, UsuarioNegocioId> {
    List<UsuarioNegocio> findById_IdUsuario(String idUsuario);
    Optional<UsuarioNegocio> findById_IdUsuarioAndId_IdNegocio(String idUsuario, String idNegocio);
}
