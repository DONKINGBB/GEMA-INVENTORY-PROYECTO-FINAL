package com.example.gemainventory.data.local

import androidx.room.TypeConverter
import com.example.gemainventory.data.local.entity.DetallePedidoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetallePedidoConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromDetalleList(value: List<DetallePedidoEntity>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDetalleList(value: String?): List<DetallePedidoEntity>? {
        if (value == null) return null
        val type = object : TypeToken<List<DetallePedidoEntity>>() {}.type
        return gson.fromJson(value, type)
    }
}
