package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @Column(name = "correo", unique = true)
    private String correo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contrase\u00f1a")
    private String passwordHash;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "notify_low_stock")
    @Builder.Default
    private Boolean notifyLowStock = true;

    @Column(name = "notify_new_orders")
    @Builder.Default
    private Boolean notifyNewOrders = true;

    @Column(name = "notify_inventory_changes")
    @Builder.Default
    private Boolean notifyInventoryChanges = true;

    // --- NEXO CON EL NEGOCIO (MULTI-TENANT) ---
    @Column(name = "id_negocio")
    private String idNegocio;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;
}