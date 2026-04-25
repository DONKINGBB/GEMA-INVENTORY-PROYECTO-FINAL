package com.example.demo.service;

import com.example.demo.model.Reporte;
import java.util.List;

public interface ReporteService {
    List<Reporte> getAll();
    Reporte getById(String id);
    Reporte save(Reporte reporte);
    void delete(String id);
    Reporte update(String id, Reporte reporte);
}