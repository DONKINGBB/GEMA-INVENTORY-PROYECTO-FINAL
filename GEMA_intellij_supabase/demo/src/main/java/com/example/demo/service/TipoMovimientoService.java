package com.example.demo.service;

import com.example.demo.model.TipoMovimiento;
import java.util.List;

public interface TipoMovimientoService {
    List<TipoMovimiento> getAll();
    TipoMovimiento getById(Integer id); // <-- CAMBIO: Integer
    TipoMovimiento save(TipoMovimiento tipoMovimiento);
    void delete(Integer id); // <-- CAMBIO: Integer
    TipoMovimiento update(Integer id, TipoMovimiento tipoMovimiento); // <-- CAMBIO: Integer
}