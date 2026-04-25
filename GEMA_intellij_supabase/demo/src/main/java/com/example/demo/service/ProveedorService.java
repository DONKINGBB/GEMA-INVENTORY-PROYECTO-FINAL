package com.example.demo.service;

import com.example.demo.model.Proveedor;
import java.util.List;

public interface ProveedorService {
    List<Proveedor> getAll();
    Proveedor getById(String id);
    Proveedor save(Proveedor proveedor);
    void delete(String id);
    Proveedor update(String id, Proveedor proveedor);
    List<Proveedor> listarPorNegocio(String idNegocio);
}