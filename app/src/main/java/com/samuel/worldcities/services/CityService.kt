package com.samuel.worldcities.services

import com.samuel.worldcities.responses.CityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {
    @GET("v1/city")
    suspend fun getCities(
        @Query("page") page: Int,
        @Query("include") include: String?,
        @Query("filter[0][name][contains]") filter: String?
    ): Response<CityResponse>
}