package com.example.demo.repository;

import com.example.demo.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, String> {
    List<Actividad> findByIdNegocio(String idNegocio);
    List<Actividad> findByIdUsuario(String idUsuario);
}
