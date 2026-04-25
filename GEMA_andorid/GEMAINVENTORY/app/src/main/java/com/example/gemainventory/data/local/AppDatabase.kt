package com.example.gemainventory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gemainventory.data.local.dao.AlmacenDao
import com.example.gemainventory.data.local.dao.CategoriaDao
import com.example.gemainventory.data.local.dao.ClienteDao
import com.example.gemainventory.data.local.dao.PedidoDao
import com.example.gemainventory.data.local.dao.ProductoDao
import com.example.gemainventory.data.local.entity.AlmacenEntity
import com.example.gemainventory.data.local.entity.CategoriaEntity
import com.example.gemainventory.data.local.entity.ClienteEntity
import com.example.gemainventory.data.local.entity.PedidoEntity
import com.example.gemainventory.data.local.entity.ProductoEntity

@Database(entities = [
    ProductoEntity::class,
    CategoriaEntity::class,
    AlmacenEntity::class,
    ClienteEntity::class,
    PedidoEntity::class
], version = 4, exportSchema = false)
@TypeConverters(SyncStateConverter::class, DetallePedidoConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun almacenDao(): AlmacenDao
    abstract fun clienteDao(): ClienteDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gema_inventory_offline_db"
                )
                .fallbackToDestructiveMigration() // Only for alpha/dev phase
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
