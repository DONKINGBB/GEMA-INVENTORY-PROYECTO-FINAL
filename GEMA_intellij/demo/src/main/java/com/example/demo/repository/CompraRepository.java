package com.example.demo.repository;

import com.example.demo.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, String> {
    
    java.util.List<Compra> findByIdNegocio(String idNegocio);

    // Suma total de compras (gastos) por Negocio
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.total), 0.0) FROM Compra c WHERE c.idNegocio = :idNegocio")
    Double sumTotalByIdNegocio(String idNegocio);

    // Suma total de compras (gastos) por mes y año por Negocio
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.total), 0.0) FROM Compra c WHERE c.idNegocio = :idNegocio AND MONTH(c.fechaCompra) = :month AND YEAR(c.fechaCompra) = :year")
    Double sumTotalByMonthAndYear(int month, int year, String idNegocio);
}