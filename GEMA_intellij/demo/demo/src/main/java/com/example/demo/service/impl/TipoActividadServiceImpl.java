package com.example.demo.service.impl;

import com.example.demo.model.TipoActividad;
import com.example.demo.repository.TipoActividadRepository;
import com.example.demo.service.TipoActividadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TipoActividadServiceImpl implements TipoActividadService {

    private final TipoActividadRepository tipoActividadRepository;

    @Override
    public List<TipoActividad> getAll() {
        return tipoActividadRepository.findAll();
    }

    @Override
    public TipoActividad getById(Integer id) {
        return tipoActividadRepository.findById(id).orElse(null);
    }

    @Override
    public TipoActividad save(TipoActividad tipoActividad) {
        return tipoActividadRepository.save(tipoActividad);
    }

    @Override
    public void delete(Integer id) {
        tipoActividadRepository.deleteById(id);
    }

    @Override
    public TipoActividad update(Integer id, TipoActividad tipoActividad) {
        TipoActividad existente = tipoActividadRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombre(tipoActividad.getNombre());
            return tipoActividadRepository.save(existente);
        }
        return null;
    }
}
