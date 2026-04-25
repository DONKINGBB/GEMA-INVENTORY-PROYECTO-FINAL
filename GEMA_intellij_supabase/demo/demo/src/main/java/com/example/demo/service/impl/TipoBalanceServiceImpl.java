package com.example.demo.service.impl;

import com.example.demo.model.TipoBalance;
import com.example.demo.repository.TipoBalanceRepository;
import com.example.demo.service.TipoBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoBalanceServiceImpl implements TipoBalanceService {

    private final TipoBalanceRepository repository;

    @Override
    public List<TipoBalance> getAll() {
        return repository.findAll();
    }

    @Override
    public TipoBalance getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public TipoBalance save(TipoBalance tipo) {
        return repository.save(tipo);
    }

    @Override
    public TipoBalance update(Integer id, TipoBalance tipo) {
        if (!repository.existsById(id)) {
            return null;
        }
        tipo.setId(id);
        return repository.save(tipo);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
