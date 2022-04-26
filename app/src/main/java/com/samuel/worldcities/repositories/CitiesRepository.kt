package com.samuel.worldcities.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.samuel.worldcities.database.AppDatabase
import com.samuel.worldcities.models.City
import com.samuel.worldcities.services.CityService
import javax.inject.Inject

class CitiesRepository @Inject constructor(
    private val cityService: CityService,
    private val database: AppDatabase
) {

    private var config = PagingConfig(pageSize = 15, initialLoadSize = 15)

    companion object {
        private val COUNTRY = "country"
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getCities(name: String?, newData: Boolean): Pager<Int, City> {
        return Pager(
            config,
            remoteMediator = CityRemoteMediator(name ?: "", newData, database, cityService)
        ) {
            database.cityDao().pagingSource()
        }
    }

}

