package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoSeleccionDto {
    private String idProducto;
    private String nombre;
    private String sku;
    private Double precioVenta;
    private Integer stockActual;
    private String imagenUrl;
}
