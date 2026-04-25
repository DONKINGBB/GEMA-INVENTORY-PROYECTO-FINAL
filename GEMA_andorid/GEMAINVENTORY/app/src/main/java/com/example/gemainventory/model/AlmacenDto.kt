package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class AlmacenDto @JvmOverloads constructor(
    @SerializedName("idAlmacen")
    val idAlmacen: Int,

    @SerializedName("nombre")
    var nombre: String,

    @SerializedName("direccion")
    var direccion: String? = null,

    @SerializedName("latitud")
    var latitud: Double? = null,

    @SerializedName("longitud")
    var longitud: Double? = null
) {
    override fun toString(): String {
        return nombre
    }
}