package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class NegocioDto(
    @SerializedName("idNegocio")
    var idNegocio: String? = null,
    
    @SerializedName("nombre")
    var nombre: String? = null,
    
    @SerializedName("codigoInvitacion")
    var codigoInvitacion: String? = null
)
