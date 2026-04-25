package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private String id; // Se ignora al crear (es automático)
    private String idCliente;
    private String idUsuario;
    private Integer idAlmacenOrigen;
    private Timestamp fechaPedido;
    private Integer idEstado;
    private BigDecimal total;
    private String nombre;
    private java.time.LocalDate fechaLimite;
    private List<DetallePedidoDto> detalles;
}