package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class InventarioDto(
    @SerializedName("idInventario") val idInventario: String,
    @SerializedName("idProducto") val idProducto: String,
    @SerializedName("nombreProducto") val nombreProducto: String,
    @SerializedName("cantidadActual") val cantidadActual: Int,
    @SerializedName(value = "idAlmacen", alternate = ["id_almacen", "almacen_id"])
    val idAlmacen: Int?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("precioCompra") val precioCompra: Double?,
    @SerializedName("precioVenta") val precioVenta: Double?,
    @SerializedName("stockMinimo") val stockMinimo: Int?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("nombreAlmacen") val nombreAlmacen: String?,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?
)