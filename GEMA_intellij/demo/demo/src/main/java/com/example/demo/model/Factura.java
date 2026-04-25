package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_factura")
    private String idFactura;

    // Relación con Cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    // Usamos BigDecimal para dinero (es más preciso que Double)
    @Column(name = "total")
    private BigDecimal total;

    // Relación con el Estado (Pagada, Pendiente, etc.)
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private CatEstadosFactura estado;
}