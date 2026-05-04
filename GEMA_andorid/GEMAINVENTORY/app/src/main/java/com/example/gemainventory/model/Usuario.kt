package com.example.gemainventory.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Usuario @JvmOverloads constructor(
    @SerializedName("id")
    var idUsuario: String? = null,
    @SerializedName("nombre")
    var nombre: String? = null,
    @SerializedName("correo", alternate = ["usuario_correo", "correo_electronico", "correoElectronico", "mail", "Email", "Correo"])
    var correo: String? = null,
    @SerializedName("email", alternate = ["user_email", "email_address", "emailAddress"])
    var email: String? = null,
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