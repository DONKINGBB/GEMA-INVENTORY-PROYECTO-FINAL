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
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private Integer stockMinimo;
    private String categoria;
    private String imagenUrl;
    private String fechaCreacion;
}