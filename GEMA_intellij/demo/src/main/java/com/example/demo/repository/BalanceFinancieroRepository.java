package com.example.demo.repository;

import com.example.demo.model.BalanceFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceFinancieroRepository extends JpaRepository<BalanceFinanciero, String> {
    
    // Buscar balances por negocio O registros "huérfanos" (nulos) para recuperación
    @org.springframework.data.jpa.repository.Query("SELECT b FROM BalanceFinanciero b WHERE b.idNegocio = :idNegocio OR b.idNegocio IS NULL")
    java.util.List<BalanceFinanciero> findByNegocioAutoRecover(String idNegocio);

    // HE BORRADO LA QUERY PARA QUE EL SERVIDOR ARRANQUE
    // @Query(...)
    // Double calcularBeneficioMesActual();
}