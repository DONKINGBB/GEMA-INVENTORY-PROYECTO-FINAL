package com.example.demo.service.impl;

import com.example.demo.model.Reporte;
import com.example.demo.repository.ReporteRepository;
import com.example.demo.service.ReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    @Override
    public List<Reporte> getAll() {
        return reporteRepository.findAll();
    }

    @Override
    public Reporte getById(String id) {
        return reporteRepository.findById(id).orElse(null);
    }

    @Override
    public Reporte save(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    @Override
    public void delete(String id) {
        reporteRepository.deleteById(id);
    }

    @Override
    public Reporte update(String id, Reporte reporte) {
        Reporte aux = reporteRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setIdTipo(reporte.getIdTipo());
            aux.setIdFormato(reporte.getIdFormato());
            aux.setGeneradoPor(reporte.getGeneradoPor());
            // No actualizamos fechaGeneracion
            return reporteRepository.save(aux);
        }
        return null;
    }
}