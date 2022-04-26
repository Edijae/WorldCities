package com.samuel.worldcities.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samuel.worldcities.models.City

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    fun getAll(): List<City>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<City>)

    @Query("SELECT * FROM city")
    fun pagingSource(): PagingSource<Int, City>

    @Query("DELETE FROM city")
    suspend fun clearAll()

    @Query("DELETE FROM city WHERE name LIKE :query")
    fun deleteByQuery(query: String)
}