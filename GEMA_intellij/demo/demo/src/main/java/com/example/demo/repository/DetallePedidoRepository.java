package com.example.demo.repository;

import com.example.demo.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, String> {

    List<DetallePedido> findByIdPedido(String idPedido);
}
