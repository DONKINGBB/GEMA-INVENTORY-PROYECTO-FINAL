package com.example.gemainventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey
    val idPedido: String, // String UUID
    val idCliente: String,
    val idAlmacenOrigen: String,
    val detalles: List<DetallePedidoEntity>?,
    val fechaPedido: String?,
    val total: Double?,
    val idEstado: Int?,
    val nombre: String?,
    val fechaLimite: String?,
    
    // Offline Sync Fields
    val syncState: SyncState = SyncState.SYNCED,
    val updatedAt: Long = System.currentTimeMillis()
)
