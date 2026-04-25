package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class CategoriaDto(
    @SerializedName("idCategoria")
    val idCategoria: Int,

    @SerializedName("nombre")
    var nombre: String,

    @SerializedName("descripcion")
    var descripcion: String? = null
) {
    override fun toString(): String {
        return nombre
    }
}