package com.example.demo.service.impl;

import com.example.demo.model.Actividad;
import com.example.demo.repository.ActividadRepository;
import com.example.demo.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;

    @Override
    public List<Actividad> getAll() {
        return actividadRepository.findAll();
    }

    @Override
    public Actividad getById(String id) {
        return actividadRepository.findById(id).orElse(null);
    }

    @Override
    public Actividad save(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    @Override
    public Actividad update(String id, Actividad actividad) {
        if (!actividadRepository.existsById(id)) {
            return null;
        }
        actividad.setId(id);
        return actividadRepository.save(actividad);
    }

    @Override
    public void delete(String id) {
        actividadRepository.deleteById(id);
    }

    @Override
    public List<Actividad> listarPorNegocio(String idNegocio) {
        return actividadRepository.findByIdNegocio(idNegocio);
    }
}
