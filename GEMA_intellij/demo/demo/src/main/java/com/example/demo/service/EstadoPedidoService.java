package com.example.demo.service;

import com.example.demo.model.EstadoPedido;
import java.util.List;

public interface EstadoPedidoService {
    List<EstadoPedido> getAll();
    EstadoPedido getById(Integer id);
    EstadoPedido save(EstadoPedido estado);
    void delete(Integer id);
    EstadoPedido update(Integer id, EstadoPedido estado);
}
