package com.example.demo.repository;

import com.example.demo.model.TipoBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoBalanceRepository extends JpaRepository<TipoBalance, Integer> {
}
