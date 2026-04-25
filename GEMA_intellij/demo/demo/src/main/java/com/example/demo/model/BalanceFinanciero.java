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
@Table(name = "balance_financiero")
public class BalanceFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id_balance")
    private String idBalance;

    @ManyToOne
    @JoinColumn(name = "id_tipo_balance")
    private CatTiposBalance tipoBalance;

    @Column(name = "fuente")
    private String fuente;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "referencia")
    private String referencia;

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) this.fecha = LocalDateTime.now();
    }
}