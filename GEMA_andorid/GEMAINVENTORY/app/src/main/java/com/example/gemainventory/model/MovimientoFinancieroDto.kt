package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovimientoFinancieroDto(
    @SerializedName("id_movimiento")
    val idMovimiento: String? = null,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("monto")
    val monto: Double,

    @SerializedName("tipo")
    val tipo: String, // "INGRESO" o "GASTO"

    @SerializedName("fecha")
    val fecha: String, // Formato sugerido: "yyyy-MM-dd" o "dd/MM/yyyy"

    @SerializedName("id_usuario")
    val idUsuario: String
) : Serializable {
    // En Kotlin no hace falta escribir Getters/Setters ni toString,
    // la "data class" ya hace todo eso automáticamente.
}