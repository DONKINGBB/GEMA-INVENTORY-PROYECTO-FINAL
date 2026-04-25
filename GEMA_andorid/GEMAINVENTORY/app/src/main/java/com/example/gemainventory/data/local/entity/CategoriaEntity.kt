package com.example.gemainventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey
    val idCategoria: String, // String UUID for offline creation
    val nombre: String,
    val descripcion: String?,
    
    // Offline Sync Fields
    val syncState: SyncState = SyncState.SYNCED,
    val updatedAt: Long = System.currentTimeMillis()
)
