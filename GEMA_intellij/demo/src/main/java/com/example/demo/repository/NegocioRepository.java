package com.example.demo.repository;

import com.example.demo.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, String> {
    Optional<Negocio> findByCodigoInvitacion(String codigoInvitacion);
    boolean existsByNombre(String nombre);
}
