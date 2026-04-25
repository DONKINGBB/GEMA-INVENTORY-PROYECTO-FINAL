package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class ProveedorDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("nombre")
    var nombre: String,

    @SerializedName("contacto")
    var contacto: String? = null,

    @SerializedName("direccion")
    var direccion: String? = null
) {
    override fun toString(): String {
        return nombre
    }
}
