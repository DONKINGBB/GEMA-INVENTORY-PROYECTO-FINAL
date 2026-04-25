package com.example.demo.service;

import com.example.demo.model.TipoActividad;
import java.util.List;

public interface TipoActividadService {
    List<TipoActividad> getAll();
    TipoActividad getById(Integer id);
    TipoActividad save(TipoActividad tipoActividad);
    void delete(Integer id);
    TipoActividad update(Integer id, TipoActividad tipoActividad);
}
