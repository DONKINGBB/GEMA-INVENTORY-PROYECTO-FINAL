package com.example.demo.service;

import com.example.demo.model.BalanceFinanciero;

import java.util.List;

public interface BalanceFinancieroService {
    List<BalanceFinanciero> getAll();
    BalanceFinanciero getById(String id);
    BalanceFinanciero save(BalanceFinanciero balance);
    BalanceFinanciero update(String id, BalanceFinanciero balance);
    void delete(String id);
}
