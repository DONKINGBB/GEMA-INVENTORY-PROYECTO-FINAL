package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class CompraDto {
    private String id;
    private String idProveedor;
    private String nombreProveedor;
    private String idUsuario;
    private Integer idAlmacenDestino;
    private Timestamp fechaCompra; // Opcional, solo para mostrar
    private BigDecimal total;
}