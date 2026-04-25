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
    private Integer productos_bajo_stock; // Renamed
    private Double beneficio_mes;
}