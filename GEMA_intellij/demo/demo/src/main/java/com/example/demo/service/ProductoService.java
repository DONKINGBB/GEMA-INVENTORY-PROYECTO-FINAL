package com.example.demo.service;

import com.example.demo.dto.ProductoDto; // Necesitarás crear este DTO
import com.example.demo.model.Producto;

public interface ProductoService {
    Producto crearProductoCompleto(ProductoDto dto);
}