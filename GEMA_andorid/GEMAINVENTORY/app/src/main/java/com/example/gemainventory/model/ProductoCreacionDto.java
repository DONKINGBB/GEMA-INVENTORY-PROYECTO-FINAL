package com.example.gemainventory.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// ESTE ES SOLO PARA CREAR (Enviar al servidor)
public class ProductoCreacionDto implements Serializable {

    // Al crear no mandamos ID de producto, se crea solo.

    @SerializedName("nombre") private String nombre;
    @SerializedName("sku") private String sku;
    @SerializedName("cantidad") private Integer cantidad;
    @SerializedName("categoria") private String categoria;
    @SerializedName("precioCompra") private Double precioCompra;
    @SerializedName("precioVenta") private Double precioVenta;
    @SerializedName("descripcion") private String descripcion;
    @SerializedName("stockMinimo") private Integer stockMinimo;

    // ESTE ES EL IMPORTANTE PARA QUE NO DE ERROR 500
    // Tu servidor espera "idUsuario" o "id_usuario".
    // Al ponerlo así, cubrimos ambas posibilidades para escritura.
    @SerializedName(value = "idUsuario", alternate = {"id_usuario"})
    private String idUsuario;

    @SerializedName("imagenUrl")
    private String imagenUrl;
    
    @SerializedName("modelo3dUrl")
    private String modelo3dUrl;

    @SerializedName(value = "id_almacen", alternate = {"idAlmacen", "almacen_id", "almacenId"})
    private Integer idAlmacen;

    public ProductoCreacionDto(String nombre, String sku, Integer cantidad, String categoria,
                               Double precioCompra, Double precioVenta, String descripcion,
                               Integer stockMinimo, String idUsuario, Integer idAlmacen) {
        this.nombre = nombre;
        this.sku = sku;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.descripcion = descripcion;
        this.stockMinimo = stockMinimo;
        this.idUsuario = idUsuario;
        this.idAlmacen = idAlmacen;
    }

    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getImagenUrl() { return imagenUrl; }
    public void setModelo3dUrl(String modelo3dUrl) { this.modelo3dUrl = modelo3dUrl; }
    public String getModelo3dUrl() { return modelo3dUrl; }
}