package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MovimientoInventarioDto {
    private String id;
    private String idProducto;
    private Integer idAlmacen;
    private String idUsuario;
    private Integer idTipoMovimiento;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private Timestamp fechaMovimiento; // Opcional, solo para mostrar
    private String referencia;
    private String observaciones;
}