package com.example.demo.service;

import com.example.demo.model.Pedido;
import java.util.List;

public interface PedidoService {
    List<Pedido> getAll();
    Pedido getById(String id);
    Pedido save(Pedido pedido);
    void delete(String id);
    Pedido update(String id, Pedido pedido);
}
