package com.example.demo.service;

import com.example.demo.dto.ProductoDto; // Necesitarás crear este DTO
import com.example.demo.model.Producto;

import java.util.List;

public interface ProductoService {
    Producto crearProductoCompleto(ProductoDto dto);
    Producto actualizarProducto(String id, ProductoDto dto);
    void eliminarProducto(String id);
    List<Producto> listarPorUsuario(String idUsuario);
    ProductoDto obtenerProductoPorId(String id); // Return DTO with warehouse info
}