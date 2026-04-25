package com.example.demo.service;

import com.example.demo.model.CategoriaProducto;
import java.util.List;

public interface CategoriaProductoService {
    List<CategoriaProducto> getAll();
    CategoriaProducto getById(Integer id); // <-- CAMBIO: Integer
    CategoriaProducto save(CategoriaProducto categoriaProducto);
    void delete(Integer id); // <-- CAMBIO: Integer
    CategoriaProducto update(Integer id, CategoriaProducto categoriaProducto); // <-- CAMBIO: Integer
}