package com.example.demo.service;

import com.example.demo.model.Actividad;

import java.util.List;

public interface ActividadService {
    List<Actividad> getAll();
    Actividad getById(String id);
    Actividad save(Actividad actividad);
    Actividad update(String id, Actividad actividad);
    void delete(String id);
}
