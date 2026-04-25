package com.example.demo.repository;

import com.example.demo.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {

    // CORRECCIÓN: Usamos 'f.estado.idEstado' para acceder al ID dentro del objeto estado
    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f WHERE f.estado.idEstado = :idEstado")
    Double sumarTotalPorEstado(Integer idEstado);
}