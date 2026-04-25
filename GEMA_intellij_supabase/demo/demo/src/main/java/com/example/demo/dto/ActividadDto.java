package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActividadDto {
    private String id;
    private String idUsuario;
    private Integer idTipoActividad;
    private String descripcion;
    private LocalDateTime fecha;
    private String entidadAfectada;
}
