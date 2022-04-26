package com.samuel.worldcities.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.samuel.worldcities.R
import com.samuel.worldcities.databinding.MarkerViewBinding
import com.samuel.worldcities.models.City

class MarkerManager(
    private val googleMap: GoogleMap,
    val context: Context
) {
    private var position = -1
    private var selectedMarkerId: Int = 0
    private var markers = HashMap<Int, Marker>()
    private var items: ArrayList<City> = ArrayList()
    private var latLngs: ArrayList<LatLng> = ArrayList()
    private var builder = LatLngBounds.Builder()

    private fun addMarker(
        city: City,
        lng: LatLng
    ) {
        var marker = getMarker(city.id)
        val markerView = createMarkerView(city)
        if (marker == null) {
            val markerOptions = MarkerOptions()
                .position(lng)
                .title(city.name)
                .anchor(0.5f, 0.5f)
                .snippet(city.id.toString())
                .visible(true)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        markerView
                    )
                )
            marker = googleMap.addMarker(markerOptions)
            markers[city.id] = marker!!
        } else {
            marker.position = lng
            marker.title = city.name
            marker.setAnchor(0.5f, 0.5f)
            marker.snippet = city.id.toString()
            marker.isVisible = true
            marker.setIcon(
                BitmapDescriptorFactory.fromBitmap(
                    markerView
                )
            )
        }
        city.marker = marker
    }


    private fun createMarkerView(city: City): Bitmap {
        val markerView = MarkerViewBinding.inflate(LayoutInflater.from(context), null, false)
        var icon: Int? = null

        val background: Int = if (city.isSelected) {
            R.drawable.selected_marker_background
        } else {
            R.drawable.default_marker_background
        }

        icon?.let {
            markerView.iconImageView.setImageResource(it)
        }
        markerView.iconImageView.setBackgroundResource(background)
        markerView.cityTv.text = city.name
        return getBitmapFromView(markerView.root)
    }

    fun addMarkers(
        _listings: List<City>, new: Boolean
    ) {
        selectedMarkerId = -1
        if (new) {
            this.items.clear()
            builder = LatLngBounds.builder()
            clear()
        }

        for (item in _listings) {
            val lng = LatLng(item.lat, item.lng)
            if (!PointInPolygon.contains(lng, latLngs)) {
                latLngs.add(lng)
                builder.include(lng)
            }
            addMarker(
                item, lng
            )
            this.items.add(item)
        }
        val cameraUpdate = CameraUpdateFactory.zoomOut()
        googleMap.animateCamera(cameraUpdate)
    }

    fun clear() {
        markers.clear()
        googleMap.clear()
    }

    private fun getCity(listingId: Int): Pair<Int, City?> {
        val item = items[listingId]
        if (item != null) {
            if (item.id == listingId) {
                position = -1
                return Pair(-1, item)
            }
        }
        return Pair(-1, null)
    }

    private fun getMarker(listingId: Int): Marker? {
        var marker: Marker? = null
        try {
            marker = markers.get(listingId)
        } catch (exception: IndexOutOfBoundsException) {
        }
        return marker
    }

    companion object {

        fun getBitmapFromView(view: View): Bitmap {
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val measuredWidth = view.measuredWidth
            val measuredHeight = view.measuredHeight

            view.layout(0, 0, measuredWidth, measuredHeight)
            val returnedBitmap = Bitmap.createBitmap(
                measuredWidth, measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            view.background?.draw(canvas)
            view.draw(canvas)
            return returnedBitmap
        }
    }
}