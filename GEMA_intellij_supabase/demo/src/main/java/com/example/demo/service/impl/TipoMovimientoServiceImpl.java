package com.example.demo.service.impl;

import com.example.demo.model.TipoMovimiento;
import com.example.demo.repository.TipoMovimientoRepository;
import com.example.demo.service.TipoMovimientoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TipoMovimientoServiceImpl implements TipoMovimientoService {

    private final TipoMovimientoRepository tipoMovimientoRepository;

    @Override
    public List<TipoMovimiento> getAll() {
        return tipoMovimientoRepository.findAll();
    }

    @Override
    public TipoMovimiento getById(Integer id) { // <-- CAMBIO: Integer
        return tipoMovimientoRepository.findById(id).orElse(null);
    }

    @Override
    public TipoMovimiento save(TipoMovimiento tipoMovimiento) {
        return tipoMovimientoRepository.save(tipoMovimiento);
    }

    @Override
    public void delete(Integer id) { // <-- CAMBIO: Integer
        tipoMovimientoRepository.deleteById(id);
    }

    @Override
    public TipoMovimiento update(Integer id, TipoMovimiento tipoMovimiento) { // <-- CAMBIO: Integer
        TipoMovimiento aux = tipoMovimientoRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setNombre(tipoMovimiento.getNombre());
            return tipoMovimientoRepository.save(aux);
        }
        return null;
    }
}