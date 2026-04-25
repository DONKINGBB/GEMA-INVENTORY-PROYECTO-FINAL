package com.example.demo.service;

import com.example.demo.model.DetallePedido;
import java.util.List;

public interface DetallePedidoService {
    List<DetallePedido> getAll();

    DetallePedido getById(String id); // <--- String

    DetallePedido save(DetallePedido detallePedido);

    DetallePedido update(String id, DetallePedido detallePedido); // <--- String

    void delete(String id); // <--- String
}