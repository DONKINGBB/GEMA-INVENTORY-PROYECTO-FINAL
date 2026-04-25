package com.example.demo.service;

import com.example.demo.model.DetalleCompra;
import java.util.List;

public interface DetalleCompraService {
    List<DetalleCompra> getAll();
    DetalleCompra getById(String id);
    DetalleCompra save(DetalleCompra detalleCompra);
    void delete(String id);
    DetalleCompra update(String id, DetalleCompra detalleCompra);
}