package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

class Usuario @JvmOverloads constructor(
    @SerializedName("id")
    var idUsuario: String? = null,
    @SerializedName("nombre")
    var nombre: String? = null,
    @SerializedName("user")
    var correo: String? = null,
    @SerializedName("direccion")
    var direccion: String? = null,
    @SerializedName("telefono")
    var telefono: String? = null,
    @SerializedName("idRol")
    var idRol: Int? = null,
    @SerializedName("contraseña")
    var password: String? = null,
    @SerializedName("fcmToken")
    var fcmToken: String? = null,
    @SerializedName("notifyLowStock")
    var notifyLowStock: Boolean? = true,
    @SerializedName("notifyNewOrders")
    var notifyNewOrders: Boolean? = true,
    @SerializedName("notifyInventoryChanges")
    var notifyInventoryChanges: Boolean? = true,
    @SerializedName("idNegocio")
    var idNegocio: String? = null,
    @SerializedName("imagenUrl")
    var imagenUrl: String? = null
)