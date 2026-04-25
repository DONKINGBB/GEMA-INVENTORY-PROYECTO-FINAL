package com.example.gemainventory.data.local.entity

data class DetallePedidoEntity(
    val idProducto: String,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double
)
