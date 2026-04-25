package com.example.demo.repository;

import com.example.demo.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

// IMPORTANTE: Cambiamos Integer por String porque tu ID es un UUID
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, String> {
}