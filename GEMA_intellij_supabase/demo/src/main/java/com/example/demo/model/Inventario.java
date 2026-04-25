package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_inventario")
    private String idInventario;

    // Relación con Producto
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Relación con Almacen
    @ManyToOne
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @Column(name = "cantidad_actual")
    private Integer cantidadActual;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    // Este método se ejecuta antes de guardar para poner la fecha actual
    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}