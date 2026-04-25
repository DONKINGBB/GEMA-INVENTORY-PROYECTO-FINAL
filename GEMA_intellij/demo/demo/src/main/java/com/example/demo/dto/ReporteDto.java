package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ReporteDto {
    private String id;
    private Integer idTipo;
    private Integer idFormato;
    private Timestamp fechaGeneracion; // Opcional, solo para mostrar
    private String generadoPor;
}