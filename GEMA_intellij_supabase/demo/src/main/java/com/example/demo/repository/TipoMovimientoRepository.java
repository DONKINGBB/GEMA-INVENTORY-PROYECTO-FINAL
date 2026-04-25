package com.example.demo.repository;

import com.example.demo.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Nota el cambio: JpaRepository<TipoMovimiento, Integer>
public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {
}