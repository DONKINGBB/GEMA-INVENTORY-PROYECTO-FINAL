package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceFinancieroDto {
    private String id;
    private Integer idTipoBalance;
    private String fuente;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String referencia;
}
