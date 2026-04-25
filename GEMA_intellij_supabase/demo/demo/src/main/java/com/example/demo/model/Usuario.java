package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "correo", unique = true)
    private String user; // En Android lo mandas como 'correo', aquí lo mapeas a 'user'

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contraseña")
    private String password;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    // --- AGREGAMOS ESTOS DOS ---
    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;
}