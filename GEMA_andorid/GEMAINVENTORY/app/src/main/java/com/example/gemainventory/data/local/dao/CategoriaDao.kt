package com.example.gemainventory.data.local.dao

import androidx.room.*
import com.example.gemainventory.data.local.entity.CategoriaEntity
import com.example.gemainventory.data.local.entity.SyncState

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias WHERE syncState != :deletedState ORDER BY nombre ASC")
    suspend fun getActiveCategorias(deletedState: SyncState = SyncState.PENDING_DELETE): List<CategoriaEntity>

    @Query("SELECT * FROM categorias WHERE idCategoria = :id")
    suspend fun getCategoriaById(id: String): CategoriaEntity?

    @Query("SELECT * FROM categorias WHERE syncState != :syncedState")
    suspend fun getPendingSyncCategorias(syncedState: SyncState = SyncState.SYNCED): List<CategoriaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: CategoriaEntity)

    @Update
    suspend fun updateCategoria(categoria: CategoriaEntity)

    @Query("UPDATE categorias SET syncState = :state WHERE idCategoria = :id")
    suspend fun markAsDeleted(id: String, state: SyncState = SyncState.PENDING_DELETE)

    @Query("UPDATE categorias SET syncState = :state WHERE idCategoria IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, state: SyncState = SyncState.SYNCED)

    @Query("DELETE FROM categorias WHERE idCategoria IN (:ids)")
    suspend fun deleteHard(ids: List<String>)
}
