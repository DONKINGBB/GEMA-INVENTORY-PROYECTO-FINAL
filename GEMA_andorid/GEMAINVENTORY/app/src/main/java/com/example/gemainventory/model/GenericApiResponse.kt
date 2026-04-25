package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class GenericApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)