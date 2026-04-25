package com.example.gemainventory.data.local.dao

import androidx.room.*
import com.example.gemainventory.data.local.entity.PedidoEntity
import com.example.gemainventory.data.local.entity.SyncState

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos WHERE syncState != :deletedState ORDER BY fechaPedido DESC")
    suspend fun getActivePedidos(deletedState: SyncState = SyncState.PENDING_DELETE): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE idPedido = :id")
    suspend fun getPedidoById(id: String): PedidoEntity?

    @Query("SELECT * FROM pedidos WHERE syncState != :syncedState")
    suspend fun getPendingSyncPedidos(syncedState: SyncState = SyncState.SYNCED): List<PedidoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)

    @Update
    suspend fun updatePedido(pedido: PedidoEntity)

    @Query("UPDATE pedidos SET syncState = :state WHERE idPedido = :id")
    suspend fun markAsDeleted(id: String, state: SyncState = SyncState.PENDING_DELETE)

    @Query("UPDATE pedidos SET syncState = :state WHERE idPedido IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, state: SyncState = SyncState.SYNCED)

    @Query("DELETE FROM pedidos WHERE idPedido IN (:ids)")
    suspend fun deleteHard(ids: List<String>)
}
