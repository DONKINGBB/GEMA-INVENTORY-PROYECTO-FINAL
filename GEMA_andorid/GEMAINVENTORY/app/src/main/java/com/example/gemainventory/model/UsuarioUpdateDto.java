package com.example.gemainventory.model;

public class UsuarioUpdateDto {
    private String nombre;
    private String direccion;
    private String telefono;
    private String imagenUrl;

    public UsuarioUpdateDto(String nombre, String direccion, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public UsuarioUpdateDto(String nombre, String direccion, String telefono, String imagenUrl) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.imagenUrl = imagenUrl;
    }

    // Getters y setters (o que Jackson los use si son públicos)
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getImagenUrl() { return imagenUrl; }
}