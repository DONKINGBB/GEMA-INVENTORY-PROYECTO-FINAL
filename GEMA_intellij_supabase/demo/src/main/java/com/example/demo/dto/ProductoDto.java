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
    private Integer idAlmacen; // Campo nuevo para seleccionar almacén
    
    // --- FASE 2: MULTIMEDIA ---
    private String imagenUrl;
    private String modelo3dUrl;
}