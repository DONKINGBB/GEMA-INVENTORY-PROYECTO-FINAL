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
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_producto")
    private String idProducto;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "sku") // ¡NUEVO!
    private String sku;

    @Column(name = "categoria") // ¡NUEVO! (O id_categoria si usas tabla)
    private String categoria;

    @Column(name = "precio_compra") // ¡NUEVO!
    private Double precioCompra;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_venta")
    private Double precioVenta;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "id_negocio", nullable = false)
    private String idNegocio; // Vinculado a la EMPRESA, no al usuario individual

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "modelo_3d_url", length = 500)
    private String modelo3dUrl;

}