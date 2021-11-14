package com.iesperemaria.djessyczaplicki.proyectorutas.model

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import java.util.function.ToDoubleBiFunction


/**
 * This is the Route class, that contains the route and all its configuration.
 *
 * Requires a Context and a GoogleMap, in which it will be shown by default.
 */
class Route(
    //private var context : Context? = null,
    map : GoogleMap? = null,
    var id : String = "0",
    var name : String = "Ruta 0",
    var color : Int = -16777216, // black
    var cords : MutableList<LatLng> = mutableListOf()
){
    private val TAG = "Classes.Route"
    val gmaps : MutableList<GoogleMap> = mutableListOf()
    val polylineOptions = PolylineOptions()
    var polylines : MutableList<Polyline> = mutableListOf()

    init {
        if (map != null) addToMap(map)
        addCordToRoute(cords)
        //setRouteColor(color)
    }



    fun setRouteColor(color: Int) {
        polylineOptions.color(color)
        updateMaps()
    }

    fun updateMaps() {
        // Reset polylines list
        polylines = mutableListOf()
        for (i in 0 until gmaps.size) {
            addPolylineToMap(gmaps[i], i)
        }
    }

    fun addPolylineToMap(map : GoogleMap, i : Int) {
        polylines.add(map.addPolyline(polylineOptions))
        polylines[i].startCap = RoundCap()
        polylines[i].endCap = RoundCap()
    }

    fun addToMap(map : GoogleMap) : Boolean {
        if (gmaps.contains(map)) return false
        gmaps.add(map)
        val index = polylines.size
        addPolylineToMap(map, index)
         // CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow2), 16F)
        return true
    }

    fun encodePolyline(i : Int) : String {
        return PolyUtil.encode(cords)
    }

    fun addCordToRoute(newCords: MutableList<LatLng>) {
        for (i in 0 until newCords.size) {
            Log.i(TAG, newCords[i].toString())
            cords.add(newCords[i])
            polylineOptions.add(newCords[i])
        }
        updateMaps()
    }

    /**
     * Removes a Route/Polyline from a map.
     *
     * @return false if the route isn't on the specified map.
     */
    fun removeFromMap(map : GoogleMap) : Boolean {
        if (!gmaps.contains(map)) return false
        val index = gmaps.indexOf(map)
        // Possible issues: remove() might remove the polyline data too
        polylines[index].remove()
        gmaps.remove(map)
        return true
    }

    fun getMiddleCords() : LatLng {
        return cords[cords.size/2]
    }


    fun getBounds() : LatLngBounds{
        val builder = LatLngBounds.Builder()
        for (cord in cords) {
            builder.include(cord)
        }
        return builder.build()
    }
}
