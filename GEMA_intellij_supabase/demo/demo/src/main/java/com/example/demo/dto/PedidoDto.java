package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class PedidoDto {
    private String id;
    private String idCliente;
    private String idUsuario;
    private Integer idAlmacenOrigen;
    private Timestamp fechaPedido;
    private Integer idEstado;
    private BigDecimal total;
}
