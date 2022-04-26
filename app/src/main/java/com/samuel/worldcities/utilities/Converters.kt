package com.samuel.worldcities.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samuel.worldcities.models.Country

class Converters {
    val gson = Gson()
    val type = object : TypeToken<Country>() {}.type

    @TypeConverter
    fun countryToString(value: Country): String {
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun stringToCountry(value: String): Country {
        return gson.fromJson(value, type)
    }
}