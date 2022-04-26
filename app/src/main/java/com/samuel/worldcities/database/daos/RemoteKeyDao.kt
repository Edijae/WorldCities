package com.samuel.worldcities.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samuel.worldcities.models.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun remoteKeyById(id: Int): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun deleteById(id: Int)
}

