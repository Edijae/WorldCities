package com.samuel.worldcities

import CityFactory
import android.util.Log
import androidx.paging.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.samuel.worldcities.database.AppDatabase
import com.samuel.worldcities.models.City
import com.samuel.worldcities.repositories.CityRemoteMediator
import com.samuel.worldcities.repository.FakeCityApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CityRemoteMediatorTest {
    private val cityFactory = CityFactory()
    private val mockCities = listOf(
        cityFactory.createCity("Nairobi"),
        cityFactory.createCity("Mombasa"),
        cityFactory.createCity("Kisumu")
    )
    private val mockApi = FakeCityApi().apply {
        mockCities.forEach { city -> addCity(city) }
    }

    private val mockDb = AppDatabase.create(
        ApplicationProvider.getApplicationContext(),
        useInMemory = true
    )


    @After
    fun tearDown() {
        mockDb.clearAllTables()
        // Clear out failure message to default to the successful response.
        mockApi.failureMsg = null
        mockApi.clear()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        // Add mock results for the API to return.
        mockCities.forEach { city -> mockApi.addCity(city) }
        val remoteMediator = CityRemoteMediator(
            "Nairobi", true,
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, City>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }


    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        // To test endOfPaginationReached, don't set up the mockApi to return post
        // data here.
        val remoteMediator = CityRemoteMediator(
            "Dala", true,
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, City>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        Log.e(
            "TAG",
            "refreshLoadSuccessAndEndOfPaginationWhenNoMoreData: ${Gson().toJson(result)}",
        )
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
        // Set up failure message to throw exception from the mock API.
        mockApi.failureMsg = "Throw test failure"
        val remoteMediator = CityRemoteMediator(
            "Nairobi", true,
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, City>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

}