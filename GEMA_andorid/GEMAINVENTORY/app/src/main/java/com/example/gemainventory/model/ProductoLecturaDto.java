package com.example.gemainventory.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// ESTE LO USARÁS PARA: Listar productos en el Spinner, Ver inventario, Agregar a Pedido
public class ProductoLecturaDto implements Serializable {

    @SerializedName(value = "id_producto", alternate = {"idProducto"})
    private String idProducto;

    @SerializedName(value = "nombre", alternate = {"name", "nombreProducto", "product_name"})
    private String nombre;

    @SerializedName(value = "precio_venta", alternate = {"precioVenta"})
    private Double precioVenta;

    @SerializedName(value = "precio_compra", alternate = {"precioCompra"})
    private Double precioCompra;

    @SerializedName("sku") private String sku;
    @SerializedName("cantidad") private Integer cantidad;
    @SerializedName("categoria") private String categoria;
    @SerializedName("descripcion") private String descripcion;

    @SerializedName("imagenUrl")
    private String imagenUrl;
    
    @SerializedName("modelo3dUrl")
    private String modelo3dUrl;

    // Getters necesarios para los Spinners y Listas
    public String getNombre() { return nombre; }
    public String getIdProducto() { return idProducto; }
    public Double getPrecioVenta() { return precioVenta; }
    public String getImagenUrl() { return imagenUrl; }
    public String getModelo3dUrl() { return modelo3dUrl; }

    @Override
    public String toString() {
        return (nombre != null) ? nombre : "Sin Nombre";
    }
}