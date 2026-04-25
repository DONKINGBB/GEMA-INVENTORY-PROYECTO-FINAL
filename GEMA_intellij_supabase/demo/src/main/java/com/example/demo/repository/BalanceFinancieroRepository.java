package com.example.demo.repository;

import com.example.demo.model.BalanceFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceFinancieroRepository extends JpaRepository<BalanceFinanciero, String> {
    // HE BORRADO LA QUERY PARA QUE EL SERVIDOR ARRANQUE
    // @Query(...)
    // Double calcularBeneficioMesActual();
}