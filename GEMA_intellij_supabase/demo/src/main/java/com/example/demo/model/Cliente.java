package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_cliente")
    private String idCliente;

    private String nombre;
    private String contacto; // Email o Teléfono
    private String direccion;

    // (Opcional) Tipo de cliente
    @Column(name = "id_tipo_cliente")
    private Integer idTipoCliente;

    @Column(name = "id_negocio")
    private String idNegocio;
}