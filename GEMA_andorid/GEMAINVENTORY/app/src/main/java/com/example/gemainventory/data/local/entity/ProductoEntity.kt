package com.example.gemainventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey
    val idProducto: String,
    val nombre: String,
    val descripcion: String?,
    val sku: String?,
    val cantidad: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val stockMinimo: Int?,
    val idCategoria: Int?,
    val idAlmacen: Int?,
    
    // Offline Sync Fields
    val syncState: SyncState,
    val updatedAt: Long
)
