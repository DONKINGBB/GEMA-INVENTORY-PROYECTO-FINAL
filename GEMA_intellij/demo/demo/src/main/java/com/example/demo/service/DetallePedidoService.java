package com.example.demo.service;

import com.example.demo.model.DetallePedido;
import java.util.List;

public interface DetallePedidoService {
    List<DetallePedido> getAll();
    DetallePedido getById(String id);
    DetallePedido save(DetallePedido detalle);
    void delete(String id);
    DetallePedido update(String id, DetallePedido detalle);
}
