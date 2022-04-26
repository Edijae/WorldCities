package com.samuel.worldcities.responses

import com.samuel.worldcities.models.City

data class CityResponse(val data: CityData)

data class CityData(
    val items: List<City>?,
    val pagination: Pagination
)

data class Pagination(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)