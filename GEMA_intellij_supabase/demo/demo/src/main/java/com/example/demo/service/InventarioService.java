package com.example.demo.service;

import com.example.demo.dto.InventarioDto;
import java.util.List;

public interface InventarioService {
    List<InventarioDto> obtenerPorUsuario(String id);
    InventarioDto guardarInventario(InventarioDto dto);
    InventarioDto actualizarStock(String idInventario, Integer nuevaCantidad);
}