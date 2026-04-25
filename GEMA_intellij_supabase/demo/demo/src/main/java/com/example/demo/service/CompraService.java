package com.example.demo.service;

import com.example.demo.model.Compra;
import java.util.List;

public interface CompraService {
    List<Compra> getAll();
    Compra getById(String id);
    Compra save(Compra compra);
    void delete(String id);
    Compra update(String id, Compra compra);
}