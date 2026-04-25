package com.example.demo.service.impl;

import com.example.demo.model.BalanceFinanciero;
import com.example.demo.repository.BalanceFinancieroRepository;
import com.example.demo.service.BalanceFinancieroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceFinancieroServiceImpl implements BalanceFinancieroService {

    private final BalanceFinancieroRepository repository;

    @Override
    public List<BalanceFinanciero> getAll() {
        return repository.findAll();
    }

    @Override
    public BalanceFinanciero getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public BalanceFinanciero save(BalanceFinanciero balance) {
        return repository.save(balance);
    }

    @Override
    public BalanceFinanciero update(String id, BalanceFinanciero balance) {
        if (!repository.existsById(id)) {
            return null;
        }
        balance.setIdBalance(id);
        return repository.save(balance);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }


}
