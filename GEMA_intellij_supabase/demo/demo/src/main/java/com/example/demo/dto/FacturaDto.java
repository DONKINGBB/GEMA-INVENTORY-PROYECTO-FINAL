package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // O Date, según uses

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacturaDto {
    private String idFactura;
    private String idCliente;
    private String nombreCliente;
    private Double total;
    private Integer idEstado;
    private String fechaEmision; // Lo manejamos como String o LocalDateTime
}