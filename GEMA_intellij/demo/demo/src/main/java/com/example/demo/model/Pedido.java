package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @Column(name = "id_pedido")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "id_cliente", nullable = false)
    private String idCliente;

    @Column(name = "id_usuario")
    private String idUsuario;

    @Column(name = "id_almacen_origen", nullable = false)
    private Integer idAlmacenOrigen;

    @Column(name = "fecha_pedido")
    private Timestamp fechaPedido;

    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "total")
    private BigDecimal total;
}
