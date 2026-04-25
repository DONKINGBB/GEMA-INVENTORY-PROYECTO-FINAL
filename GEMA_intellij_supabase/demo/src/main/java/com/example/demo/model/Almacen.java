package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "almacenes")
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Integer idAlmacen;

    private String nombre;
    private String direccion;
    private Boolean activo;
    private Double latitud;
    private Double longitud;

    @Column(name = "id_negocio")
    private String idNegocio;
}