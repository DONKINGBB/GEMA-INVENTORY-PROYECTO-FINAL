package com.example.demo.repository;

import com.example.demo.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Nota el cambio: JpaRepository<CategoriaProducto, Integer>
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {
}