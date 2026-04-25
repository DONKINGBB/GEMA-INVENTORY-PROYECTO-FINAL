package com.example.demo.service;

import com.example.demo.model.MovimientoInventario;
import java.util.List;

public interface MovimientoInventarioService {
    List<MovimientoInventario> getAll();
    MovimientoInventario getById(String id);
    MovimientoInventario save(MovimientoInventario movimientoInventario);
    void delete(String id);
    MovimientoInventario update(String id, MovimientoInventario movimientoInventario);
}