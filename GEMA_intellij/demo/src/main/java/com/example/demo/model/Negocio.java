package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "negocios")
public class Negocio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_negocio")
    private String idNegocio;

    @Column(name = "nombre", unique = true)
    private String nombre;

    @Column(name = "codigo_invitacion", unique = true)
    private String codigoInvitacion;

    @Column(name = "propietario_id", nullable = false)
    private String propietarioId;
}
