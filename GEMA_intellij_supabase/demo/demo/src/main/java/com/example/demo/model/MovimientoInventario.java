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
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @Column(name = "id_movimiento")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "id_producto", nullable = false)
    private String idProducto;

    @Column(name = "id_almacen", nullable = false)
    private Integer idAlmacen;

    @Column(name = "id_usuario") // Permite nulos según DB
    private String idUsuario;

    @Column(name = "id_tipo_movimiento", nullable = false)
    private Integer idTipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "fecha_movimiento", insertable = false, updatable = false)
    private Timestamp fechaMovimiento;

    @Column(name = "referencia", length = 36)
    private String referencia; // e.g., ID de compra o pedido

    @Column(name = "observaciones")
    private String observaciones;
}