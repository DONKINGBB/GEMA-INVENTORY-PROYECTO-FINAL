package com.example.gemainventory.model
import com.google.gson.annotations.SerializedName

data class PedidoDto @JvmOverloads constructor(
    @SerializedName("idCliente") val idCliente: String,
    @SerializedName("idAlmacenOrigen") val idAlmacenOrigen: Int,
    @SerializedName("detalles") val detalles: List<DetallePedidoDto>? = null,

    @SerializedName("id") val id: String? = null,
    @SerializedName("fechaPedido") val fechaPedido: String? = null,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("idEstado") val idEstado: Int? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("fechaLimite") val fechaLimite: String? = null
)