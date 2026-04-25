package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
// Borramos la línea de org.hibernate.annotations.UuidGenerator para evitar conflictos

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @Column(name = "id_pedido")
    private String id;

    @Column(name = "id_cliente")
    private String idCliente;

    @Column(name = "id_usuario")
    private String idUsuario;

    @Column(name = "id_negocio")
    private String idNegocio;

    @Column(name = "id_almacen_origen")
    private Integer idAlmacenOrigen;

    @Column(name = "fecha_pedido")
    private Timestamp fechaPedido;

    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha_limite")
    private java.time.LocalDate fechaLimite;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
}