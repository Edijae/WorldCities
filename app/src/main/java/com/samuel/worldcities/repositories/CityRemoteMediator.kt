package com.samuel.worldcities.repositories

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.samuel.data.api.apiCall
import com.samuel.worldcities.database.AppDatabase
import com.samuel.worldcities.models.City
import com.samuel.worldcities.models.RemoteKey
import com.samuel.worldcities.services.CityService
import com.samuel.worldcities.utilities.Constants

@OptIn(ExperimentalPagingApi::class)
class CityRemoteMediator(
    private val name: String,
    private val newData: Boolean,
    private val database: AppDatabase,
    private val cityService: CityService
) : RemoteMediator<Int, City>() {
    val cityDao = database.cityDao()
    val keyDao = database.keyDao()

    override suspend fun initialize(): InitializeAction {
        return if (newData) {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, City>
    ): MediatorResult {
        // The network load method takes an optional after=<city.id>
        // parameter. For every page after the first, pass the last city
        // ID to let it continue from where it left off. For REFRESH,
        // pass null to load the first page.
        val loadKey = when (loadType) {
            LoadType.REFRESH -> {
                null
            }
            // In this worldcities, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> {
                return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }
            LoadType.APPEND -> {
                val remoteKey = database.withTransaction {
                    keyDao.remoteKeyById(1)
                }

                val nextPage: Int? = remoteKey?.nextPage

                // You must explicitly check if the page key is null when
                // appending, since null is only valid for initial load.
                // If you receive null for APPEND, that means you have
                // reached the end of pagination and there are no more
                // items to load.
                if (remoteKey?.nextPage == null) {
                    Log.e("TAG", "load: no nxt page")
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                nextPage
            }
        }

        // Suspending network load via Retrofit. This doesn't need to be
        // wrapped in a withContext(Dispatcher.IO) { ... } block since
        // Retrofit's Coroutine CallAdapter dispatches on a worker
        // thread.
        val response = apiCall {
            cityService.getCities(loadKey ?: 1, Constants.COUNTRY, name)
        }
        if (response.isSuccess()) {
            val data = response.getResultOrNull()?.data
            val cities = data?.items ?: emptyList()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    cityDao.clearAll()
                    keyDao.deleteById(1)
                }
                // Insert new cities into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                cityDao.insertAll(cities)
                keyDao.insertOrReplace(
                    RemoteKey(
                        1, data?.pagination?.current_page,
                        data?.pagination?.current_page?.plus(1)
                    )
                )
            }
            val last = (data?.pagination?.current_page == data?.pagination?.last_page)
            return MediatorResult.Success(
                endOfPaginationReached = last
            )
        } else {
            return MediatorResult.Error(response.getErrorOrNull() ?: Throwable())
        }
    }
}
