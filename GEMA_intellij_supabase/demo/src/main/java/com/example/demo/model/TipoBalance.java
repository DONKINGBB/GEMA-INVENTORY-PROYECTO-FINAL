package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_tipos_balance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_balance")
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
}
