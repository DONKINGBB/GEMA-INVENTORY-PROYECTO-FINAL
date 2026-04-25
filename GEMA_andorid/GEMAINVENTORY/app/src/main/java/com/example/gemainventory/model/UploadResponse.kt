package com.example.gemainventory.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("url")
    val url: String?,
    @SerializedName("error")
    val error: String?
)
