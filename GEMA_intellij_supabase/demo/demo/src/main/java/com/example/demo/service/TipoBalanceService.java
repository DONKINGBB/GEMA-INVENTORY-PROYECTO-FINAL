package com.example.demo.service;

import com.example.demo.model.TipoBalance;

import java.util.List;

public interface TipoBalanceService {
    List<TipoBalance> getAll();
    TipoBalance getById(Integer id);
    TipoBalance save(TipoBalance tipo);
    TipoBalance update(Integer id, TipoBalance tipo);
    void delete(Integer id);
}
