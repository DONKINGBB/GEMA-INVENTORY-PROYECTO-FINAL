package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DetallePedidoDto {
    private String id;
    private String idPedido;
    private String idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
