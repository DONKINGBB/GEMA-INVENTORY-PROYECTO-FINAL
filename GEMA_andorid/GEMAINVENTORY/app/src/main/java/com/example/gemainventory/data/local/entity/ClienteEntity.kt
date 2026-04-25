package com.example.gemainventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey
    val idCliente: String, // String UUID
    val nombre: String,
    val contacto: String?,
    val direccion: String?,
    
    // Offline Sync Fields
    val syncState: SyncState = SyncState.SYNCED,
    val updatedAt: Long = System.currentTimeMillis()
)
