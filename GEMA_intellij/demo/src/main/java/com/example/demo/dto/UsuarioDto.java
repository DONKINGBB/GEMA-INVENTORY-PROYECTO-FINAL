package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDto {
    private String id; // <-- Corregido de int a String
    private String user;
    private String nombre;
    private String password;
    private Integer idRol; // <-- Añadido
    private String fcmToken;
    private Boolean notifyLowStock;
    private Boolean notifyNewOrders;
    private Boolean notifyInventoryChanges;
    private String imagenUrl;
}