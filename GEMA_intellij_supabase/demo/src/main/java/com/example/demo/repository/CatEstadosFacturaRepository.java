package com.example.demo.repository;

import com.example.demo.model.CatEstadosFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatEstadosFacturaRepository extends JpaRepository<CatEstadosFactura, Integer> {
}
