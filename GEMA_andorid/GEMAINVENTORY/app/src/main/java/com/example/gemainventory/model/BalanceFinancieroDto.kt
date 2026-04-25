package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class BalanceFinancieroDto(
    @SerializedName("idBalance") val idBalance: String?,
    @SerializedName("idTipoBalance") val idTipoBalance: Int?, // 1 = Ingreso, 2 = Egreso
    @SerializedName("fuente") val fuente: String?,
    @SerializedName("monto") val monto: Double?,
    @SerializedName("fecha") val fecha: String?,
    @SerializedName("referencia") val referencia: String?,
    @SerializedName("idNegocio") val idNegocio: String?
)
