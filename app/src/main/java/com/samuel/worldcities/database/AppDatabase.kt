package com.samuel.worldcities.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.samuel.worldcities.database.daos.CityDao
import com.samuel.worldcities.database.daos.RemoteKeyDao
import com.samuel.worldcities.models.City
import com.samuel.worldcities.models.RemoteKey

@Database(entities = [City::class, RemoteKey::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, useInMemory: Boolean): AppDatabase {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            } else {
                Room.databaseBuilder(context, AppDatabase::class.java, "reddit.db")
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun cityDao(): CityDao
    abstract fun keyDao(): RemoteKeyDao
}