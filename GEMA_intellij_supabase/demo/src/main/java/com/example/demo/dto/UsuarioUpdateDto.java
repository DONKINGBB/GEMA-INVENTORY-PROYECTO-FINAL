package com.example.demo.dto;

import lombok.Data;

@Data
public class UsuarioUpdateDto {
    private String nombre;
    private String direccion;
    private String telefono;
    private String imagenUrl;
}