package com.example.demo.service;

import com.example.demo.dto.InventarioDto;
import com.example.demo.dto.ProductoSeleccionDto;
import java.util.List;

public interface InventarioService {
    List<InventarioDto> obtenerPorUsuario(String id);
    InventarioDto guardarInventario(InventarioDto dto);
    InventarioDto actualizarStock(String idInventario, Integer nuevaCantidad);
    List<ProductoSeleccionDto> obtenerProductosPorAlmacen(String userId, Integer idAlmacen);
}