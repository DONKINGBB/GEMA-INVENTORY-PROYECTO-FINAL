package com.example.gemainventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "almacenes")
data class AlmacenEntity(
    @PrimaryKey
    val idAlmacen: String, // String UUID
    val nombre: String,
    val direccion: String?,
    
    // Offline Sync Fields
    val syncState: SyncState = SyncState.SYNCED,
    val updatedAt: Long = System.currentTimeMillis()
)
