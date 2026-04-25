package com.example.gemainventory.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// ESTE ES SOLO PARA LEER (Mostrar en listas, agregar a pedidos)
public class ProductoSeleccionDto implements Serializable {

    @SerializedName(value = "id_producto", alternate = {"idProducto"})
    private String idProducto;

    @SerializedName(value = "nombre", alternate = {"name", "nombreProducto", "product_name"})
    private String nombre;

    @SerializedName(value = "precio_venta", alternate = {"precioVenta"})
    private Double precioVenta;

    @SerializedName(value = "precio_compra", alternate = {"precioCompra"})
    private Double precioCompra;

    @SerializedName("sku") private String sku;
    @SerializedName("categoria") private String categoria;
    @SerializedName("descripcion") private String descripcion;

    // Este campo a veces da problemas al leer si viene null, así que lo manejamos simple
    @SerializedName(value = "stock_minimo", alternate = {"stockMinimo", "minStock", "alert_level"})
    private Integer stockMinimo;
    @SerializedName(value = "cantidad", alternate = {"stock", "quantity", "qty", "existencia", "stockActual"})
    private Integer cantidad;
    public ProductoSeleccionDto() { }

    // Getters necesarios para la UI
    public String getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public Double getPrecioVenta() { return precioVenta; }

    @Override
    public String toString() {
        return (nombre != null) ? nombre : "Sin Nombre";
    }
    public Integer getStockMinimo() {
        return (stockMinimo != null) ? stockMinimo : 5; // Valor por defecto si viene nulo
    }

    public Integer getCantidad() {
        return (cantidad != null) ? cantidad : 0; // Valor por defecto si viene nulo
    }
}