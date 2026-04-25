package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDto {
    private String idProducto; // String porque en tu SQL es VARCHAR(36)
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}