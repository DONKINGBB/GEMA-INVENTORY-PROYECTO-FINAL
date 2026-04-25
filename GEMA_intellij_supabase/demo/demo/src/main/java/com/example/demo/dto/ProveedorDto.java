package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ProveedorDto {
    private String id;
    private String nombre;
    private String contacto;
    private String direccion;
    private Timestamp fechaRegistro; // Opcional, solo para mostrar
}
