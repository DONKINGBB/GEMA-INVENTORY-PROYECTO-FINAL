package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DetalleCompraDto {
    private String id;
    private String idCompra;
    private String idProducto;
    private Integer cantidad;
    private BigDecimal precioCosto;
}