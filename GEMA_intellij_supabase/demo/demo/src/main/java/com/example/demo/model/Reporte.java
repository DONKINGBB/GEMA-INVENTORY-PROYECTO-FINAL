package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @Column(name = "id_reporte")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "id_tipo", nullable = false)
    private Integer idTipo;

    @Column(name = "id_formato", nullable = false)
    private Integer idFormato;

    @Column(name = "fecha_generacion", insertable = false, updatable = false)
    private Timestamp fechaGeneracion;

    @Column(name = "generado_por", length = 36)
    private String generadoPor; // ID del Usuario
}