package com.example.demo.repository;

import com.example.demo.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AlmacenesRepository extends JpaRepository<Almacen, Integer> {
    List<Almacen> findByIdNegocio(String idNegocio);
}