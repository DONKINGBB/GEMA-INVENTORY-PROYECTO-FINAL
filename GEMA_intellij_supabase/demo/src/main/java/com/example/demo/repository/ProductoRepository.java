package com.example.demo.repository;

import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findByIdNegocio(String idNegocio);
    
    java.util.Optional<Producto> findBySkuAndIdNegocio(String sku, String idNegocio);
}