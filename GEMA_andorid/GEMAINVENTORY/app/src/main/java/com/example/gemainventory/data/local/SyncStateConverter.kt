package com.example.gemainventory.data.local

import androidx.room.TypeConverter
import com.example.gemainventory.data.local.entity.SyncState

class SyncStateConverter {
    @TypeConverter
    fun fromSyncState(value: SyncState): String {
        return value.name
    }

    @TypeConverter
    fun toSyncState(value: String): SyncState {
        return SyncState.valueOf(value)
    }
}
