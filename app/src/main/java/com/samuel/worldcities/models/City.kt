package com.samuel.worldcities.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.Marker
import com.samuel.worldcities.utilities.Converters

@Entity
@TypeConverters(Converters::class)
data class City(
    @PrimaryKey
    val id: Int,
    val name: String,
    val local_name: String?,
    val lat: Double,
    val lng: Double,
    val created_at: String,
    val updated_at: String,
    val country_id: Int,
    val country: Country
) {

    @Ignore
    var isSelected = false

    @Ignore
    var marker: Marker? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val city = other as City
        return id == city.id &&
                name == city.name &&
                local_name == city.local_name &&
                lat == city.lat &&
                lng == city.lng &&
                country == city.country
    }

    fun getLocalName(): String {
        return local_name ?: ""
    }
}