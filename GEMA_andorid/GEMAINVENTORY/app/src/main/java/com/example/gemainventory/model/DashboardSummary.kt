package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class DashboardSummary(
    @SerializedName("valor_inventario")
    val valorInventario: Double,

    @SerializedName("pedidos_pendientes")
    val pedidosPendientes: Int,

    @SerializedName("productos_bajo_stock")
    val productosBajoStock: Int,

    @SerializedName("beneficio_mes")
    val beneficioMes: Double
)