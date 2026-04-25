package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("usuario")
    val usuario: Usuario?,
    @SerializedName("token")
    val token: String?
)