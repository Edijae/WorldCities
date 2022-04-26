package com.samuel.worldcities.utilities

import com.google.android.gms.maps.model.LatLng

object PointInPolygon {
    fun contains(point: LatLng, polygonPoints: ArrayList<LatLng>): Boolean {
        if (polygonPoints.isEmpty()) {
            return false
        }
        var c = 0
        var point1 = polygonPoints[0]
        val n = polygonPoints.size

        for (i in 1..n) {
            val point2 = polygonPoints[i % n]
            if (point.longitude > Math.min(point1.longitude, point2.longitude)
                && point.longitude <= Math.max(point1.longitude, point2.longitude)
                && point.latitude <= Math.max(point1.latitude, point2.latitude)
                && point1.longitude != point2.longitude
            ) {
                val xinters =
                    (point.longitude - point1.longitude) * (point2.latitude - point1.latitude) / (point2.longitude - point1.longitude) + point1.latitude
                if (point1.latitude == point2.latitude || point.latitude <= xinters) {
                    c++
                }
            }
            point1 = point2
        }
        return c % 2 != 0
    }
}
