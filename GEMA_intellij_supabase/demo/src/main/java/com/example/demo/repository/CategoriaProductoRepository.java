package com.example.demo.repository;

import com.example.demo.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {

    // Nueva consulta filtrada por usuario
    List<CategoriaProducto> findByIdNegocio(String idNegocio);
}