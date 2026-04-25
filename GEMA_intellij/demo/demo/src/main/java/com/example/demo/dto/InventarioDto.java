package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDto {
    private String idInventario;
    private String idProducto;
    private String nombreProducto;
    private Integer idAlmacen;
    private Integer cantidadActual;
    private String sku;
}