package com.iesperemaria.djessyczaplicki.proyectorutas.model

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.iesperemaria.djessyczaplicki.proyectorutas.R


/**
 * This is the Route class, that contains the route and all its configuration.
 *
 * Requires a Context and a GoogleMap, in which it will be shown by default.
 */
class Route(
    private var context : Context,
    var mMap : GoogleMap,
    var id : Int = 0,
    var name : String = "Ruta 0",
    var color : Int = context.getColor(R.color.middle_blue_green),
    var cords : MutableList<LatLng> = mutableListOf()
){
    private val TAG = "Classes.Route"
    val polylineOptions = PolylineOptions()
    lateinit var polyline: Polyline
    var addedToMap = false

    init {
        addCordToRoute(cords)
        setRouteColor(color)
        addToMap()
    }

    fun setRouteColor(color: Int) {
        polylineOptions.color(color)
        addToMap()
    }

    fun addToMap() {
        // Borrar el antiguo polyline si ya estaba en el mapa (Singleton)
        if (addedToMap) polyline.remove()
        polyline = mMap.addPolyline(polylineOptions)
        polyline.startCap = RoundCap()
        polyline.endCap =  RoundCap() // CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow2), 16F)
        addedToMap = true
    }

    fun addCordToRoute(newCords: MutableList<LatLng>) {
        for (i in 0 until newCords.size) {
            Log.i(TAG, newCords[i].toString())
            cords.add(newCords[i])
            polylineOptions.add(newCords[i])
        }
        addToMap()
    }

    fun removeFromMap() {
        // Posible issues: remove() might remove the polyline data too
        polyline.remove()
    }
}