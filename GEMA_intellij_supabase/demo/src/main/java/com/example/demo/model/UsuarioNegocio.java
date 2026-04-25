package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios_negocios")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioNegocio {

    @EmbeddedId
    private UsuarioNegocioId id;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("idNegocio")
    @JoinColumn(name = "id_negocio")
    private Negocio negocio;

    @PrePersist
    protected void onCreate() {
        fechaUnion = LocalDateTime.now();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioNegocioId implements Serializable {
        @Column(name = "id_usuario")
        private String idUsuario;

        @Column(name = "id_negocio")
        private String idNegocio;
    }
}
