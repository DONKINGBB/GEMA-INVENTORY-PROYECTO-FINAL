package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class ProductoDto(
    @SerializedName("nombre")
    var nombre: String = "",

    @SerializedName("sku")
    var sku: String = "",

    @SerializedName("cantidad")
    var cantidad: Int = 0,

    @SerializedName("categoria")
    var categoria: String = "",

    @SerializedName("precioCompra")
    var precioCompra: Double = 0.0,

    @SerializedName("precioVenta")
    var precioVenta: Double = 0.0,

    @SerializedName("descripcion")
    var descripcion: String = "",

    @SerializedName("stockMinimo")
    var stockMinimo: Int = 5,

    @SerializedName("idUsuario")
    var usuarioId: String = "",

    @SerializedName(value = "id_almacen", alternate = ["idAlmacen", "almacen_id", "almacenId"])
    var idAlmacen: Int? = null,

    @SerializedName("nombreAlmacen")
    var nombreAlmacen: String? = null,

    @SerializedName("idProducto")
    val idProducto: String? = null,

    @SerializedName("imagenUrl")
    var imagenUrl: String? = null,

    @SerializedName("modelo3dUrl")
    var modelo3dUrl: String? = null
) {
    override fun toString(): String {
        return nombre.ifEmpty { "Sin Nombre" }
    }

    fun toCreacionDto(userId: String): ProductoCreacionDto {
        val dto = ProductoCreacionDto(
            nombre,
            sku,
            cantidad,
            categoria,
            precioCompra,
            precioVenta,
            descripcion,
            stockMinimo,
            userId,
            idAlmacen
        )
        dto.imagenUrl = imagenUrl
        dto.modelo3dUrl = modelo3dUrl
        return dto
    }
}
