package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @Column(name = "id_compra")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "id_proveedor", nullable = false)
    private String idProveedor;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_almacen_destino", nullable = false)
    private Integer idAlmacenDestino;

    @Column(name = "fecha_compra", insertable = false, updatable = false)
    private Timestamp fechaCompra;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;
}