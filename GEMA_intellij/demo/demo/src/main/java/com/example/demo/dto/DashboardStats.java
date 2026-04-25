package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStats {
    private Double valor_inventario;
    private Integer pedidos_pendientes;
    private Double facturas_por_cobrar;
    private Double beneficio_mes;
}