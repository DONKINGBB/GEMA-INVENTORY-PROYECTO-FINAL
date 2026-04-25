package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "actividades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {

    @Id
    @Column(name = "id_actividad")
    private String id;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_negocio", nullable = false)
    private String idNegocio;

    @Column(name = "id_tipo_actividad", nullable = false)
    private Integer idTipoActividad;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "entidad_afectada")
    private String entidadAfectada;


}
