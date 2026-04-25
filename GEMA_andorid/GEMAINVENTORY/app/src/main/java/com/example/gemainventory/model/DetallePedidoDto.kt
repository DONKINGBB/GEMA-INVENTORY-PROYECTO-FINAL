package com.example.gemainventory.model
import com.google.gson.annotations.SerializedName

data class DetallePedidoDto(
    @SerializedName("idProducto") val idProducto: String,
    @SerializedName("nombreProducto") val nombreProducto: String?,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precioUnitario") val precioUnitario: Double
) {
    val subtotal: Double get() = cantidad * precioUnitario
}