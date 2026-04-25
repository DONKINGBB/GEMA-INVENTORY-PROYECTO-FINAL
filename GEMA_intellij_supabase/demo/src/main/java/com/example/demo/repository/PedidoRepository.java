package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
    List<Pedido> findByIdNegocio(String idNegocio);
    List<Pedido> findByIdUsuario(String idUsuario);

    // Contar pedidos por estado y Negocio
    long countByIdEstadoAndIdNegocio(Integer idEstado, String idNegocio);

    // Sumar totales de pedidos completados (idEstado = 2) por Negocio
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.idEstado = :idEstado AND p.idNegocio = :idNegocio")
    Double sumTotalByEstadoAndIdNegocio(Integer idEstado, String idNegocio);

    // Sumar totales de pedidos completados en el mes actual por Negocio
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.idEstado = :idEstado AND p.idNegocio = :idNegocio AND MONTH(p.fechaPedido) = :month AND YEAR(p.fechaPedido) = :year")
    Double sumTotalByEstadoAndMonthAndYear(Integer idEstado, int month, int year, String idNegocio);
}