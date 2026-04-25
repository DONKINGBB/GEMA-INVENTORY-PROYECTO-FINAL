package com.example.gemainventory.data.local.dao

import androidx.room.*
import com.example.gemainventory.data.local.entity.AlmacenEntity
import com.example.gemainventory.data.local.entity.SyncState

@Dao
interface AlmacenDao {
    @Query("SELECT * FROM almacenes WHERE syncState != :deletedState ORDER BY nombre ASC")
    suspend fun getActiveAlmacenes(deletedState: SyncState = SyncState.PENDING_DELETE): List<AlmacenEntity>

    @Query("SELECT * FROM almacenes WHERE idAlmacen = :id")
    suspend fun getAlmacenById(id: String): AlmacenEntity?

    @Query("SELECT * FROM almacenes WHERE syncState != :syncedState")
    suspend fun getPendingSyncAlmacenes(syncedState: SyncState = SyncState.SYNCED): List<AlmacenEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlmacen(almacen: AlmacenEntity)

    @Update
    suspend fun updateAlmacen(almacen: AlmacenEntity)

    @Query("UPDATE almacenes SET syncState = :state WHERE idAlmacen = :id")
    suspend fun markAsDeleted(id: String, state: SyncState = SyncState.PENDING_DELETE)

    @Query("UPDATE almacenes SET syncState = :state WHERE idAlmacen IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, state: SyncState = SyncState.SYNCED)

    @Query("DELETE FROM almacenes WHERE idAlmacen IN (:ids)")
    suspend fun deleteHard(ids: List<String>)
}
