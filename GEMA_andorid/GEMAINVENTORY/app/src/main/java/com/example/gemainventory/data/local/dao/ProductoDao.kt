package com.example.gemainventory.data.local.dao

import androidx.room.*
import com.example.gemainventory.data.local.entity.ProductoEntity
import com.example.gemainventory.data.local.entity.SyncState

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos WHERE syncState != :deletedState ORDER BY nombre ASC")
    fun getActiveProductos(deletedState: SyncState = SyncState.PENDING_DELETE): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE idProducto = :id")
    fun getProductoById(id: String): ProductoEntity?

    @Query("SELECT * FROM productos WHERE syncState != :syncedState")
    fun getPendingSyncProductos(syncedState: SyncState = SyncState.SYNCED): List<ProductoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducto(producto: ProductoEntity)

    @Update
    fun updateProducto(producto: ProductoEntity)

    @Query("UPDATE productos SET syncState = :state WHERE idProducto = :id")
    fun markAsDeleted(id: String, state: SyncState = SyncState.PENDING_DELETE)

    // For when sync completes successfully
    @Query("UPDATE productos SET syncState = :state WHERE idProducto IN (:ids)")
    fun markAsSynced(ids: List<String>, state: SyncState = SyncState.SYNCED)
    
    // Hard delete after sync confirms deletion on server
    @Query("DELETE FROM productos WHERE idProducto IN (:ids)")
    fun deleteHard(ids: List<String>)
}
