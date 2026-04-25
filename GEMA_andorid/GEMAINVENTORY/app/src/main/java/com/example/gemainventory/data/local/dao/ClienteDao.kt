package com.example.gemainventory.data.local.dao

import androidx.room.*
import com.example.gemainventory.data.local.entity.ClienteEntity
import com.example.gemainventory.data.local.entity.SyncState

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes WHERE syncState != :deletedState ORDER BY nombre ASC")
    suspend fun getActiveClientes(deletedState: SyncState = SyncState.PENDING_DELETE): List<ClienteEntity>

    @Query("SELECT * FROM clientes WHERE idCliente = :id")
    suspend fun getClienteById(id: String): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE syncState != :syncedState")
    suspend fun getPendingSyncClientes(syncedState: SyncState = SyncState.SYNCED): List<ClienteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity)

    @Update
    suspend fun updateCliente(cliente: ClienteEntity)

    @Query("UPDATE clientes SET syncState = :state WHERE idCliente = :id")
    suspend fun markAsDeleted(id: String, state: SyncState = SyncState.PENDING_DELETE)

    @Query("UPDATE clientes SET syncState = :state WHERE idCliente IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, state: SyncState = SyncState.SYNCED)

    @Query("DELETE FROM clientes WHERE idCliente IN (:ids)")
    suspend fun deleteHard(ids: List<String>)
}
