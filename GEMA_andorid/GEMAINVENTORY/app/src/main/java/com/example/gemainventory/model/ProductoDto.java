package com.example.gemainventory.model;

import com.google.gson.annotations.SerializedName;

public class ProductoDto {

    @SerializedName("idProducto")
    private String idProducto;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("sku")
    private String sku;

    @SerializedName("cantidad")
    private Integer cantidad;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("precioCompra")
    private Double precioCompra;

    @SerializedName("precioVenta")
    private Double precioVenta;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("stockMinimo")
    private Integer stockMinimo;

    @SerializedName("idUsuario")
    private String idUsuario;

    @SerializedName("idAlmacen")
    private Integer idAlmacen;


    public ProductoDto(String nombre, String sku, Integer cantidad, String categoria,
                       Double precioCompra, Double precioVenta, String descripcion, Integer stockMinimo, String idUsuario, Integer idAlmacen) {
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

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdProducto() { return idProducto; }

    // Campos Multimedia
    @SerializedName("imagenUrl")
    private String imagenUrl;
    @SerializedName("modelo3dUrl")
    private String modelo3dUrl;

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getModelo3dUrl() { return modelo3dUrl; }
    public void setModelo3dUrl(String modelo3dUrl) { this.modelo3dUrl = modelo3dUrl; }

    public Double getPrecioVenta() { return precioVenta; }

    public String getSku() { return sku; }
    public Integer getCantidad() { return cantidad; }
    public String getCategoria() { return categoria; }
    public Double getPrecioCompra() { return precioCompra; }
    public String getDescripcion() { return descripcion; }
    public Integer getStockMinimo() { return stockMinimo; }
    public String getIdUsuario() { return idUsuario; }
    public Integer getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(Integer idAlmacen) { this.idAlmacen = idAlmacen; }

    @Override
    public String toString() {
        if (nombre == null) return "Sin Nombre (Error de Conexión)";
        return nombre;
    }
}