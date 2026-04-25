package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductoDto {
    // Los nombres de estas variables son las "llaves" del JSON que espera recibir
    private String nombre;
    private String sku;
    private Integer cantidad;
    private String categoria;
    private Double precioCompra;
    private Double precioVenta;
    private String descripcion;
    private Integer stockMinimo;
    private String idUsuario;
}