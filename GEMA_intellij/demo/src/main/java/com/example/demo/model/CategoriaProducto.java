package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder; // <--- ¡ESTA ES LA QUE FALTA!
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // <--- ¡AÑADE ESTO!
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cat_categorias_producto")
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "id_negocio")
    private String idNegocio;
}