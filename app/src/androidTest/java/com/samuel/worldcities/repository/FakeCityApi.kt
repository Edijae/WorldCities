/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samuel.worldcities.repository

import com.samuel.worldcities.models.City
import com.samuel.worldcities.responses.CityData
import com.samuel.worldcities.responses.CityResponse
import com.samuel.worldcities.responses.Pagination
import com.samuel.worldcities.services.CityService
import retrofit2.Response
import retrofit2.http.Query
import java.io.IOException

/**
 * implements the CityApi with controllable requests
 */
class FakeCityApi : CityService {
    // subreddits keyed by name
    private val model = mutableMapOf<String, City>()
    var failureMsg: String? = null

    fun addCity(city: City) {
        model[city.name] = city
    }

    private fun findCity(
        name: String
    ): CityResponse {

        val list = ArrayList<City>()
        model[name]?.let { list.add(it) }
        return CityResponse(CityData(list, Pagination(1, 2, 15, 15)))
    }

    override suspend fun getCities(
        @Query("page") page: Int,
        @Query("include") include: String?,
        @Query("filter[0][name][contains]") filter: String?
    ): Response<CityResponse> {
        failureMsg?.let {
            throw IOException(it)
        }
        val items = findCity(filter ?: "")
        return Response.success(200, items)
    }

    fun clear() {
        model.clear()
    }

}