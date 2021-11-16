package com.iesperemaria.djessyczaplicki.proyectorutas.model

import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil


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
    color : Int = -16777216, // black
    newCords : MutableList<LatLng> = mutableListOf()
){
    private val TAG = "Classes.Route"
    var color : Int = -16777216
        private set
    val cords : MutableList<LatLng> = mutableListOf()
    private val gmaps : MutableList<GoogleMap> = mutableListOf()
    private var polylineOptions = PolylineOptions()
    private var polylines : MutableList<Polyline> = mutableListOf()
    private var markers: MutableList<Marker> = mutableListOf()
    var lastMarkerPos: LatLng? = null
    var lastId: Int = -1

    init {
        if (map != null) addToMap(map)
        // I use "newCords" to add the cords to the map, because otherwise,
        // they would be added twice to the cords list
        addCords(newCords)
        setRouteColor(color)
    }



    private fun setRouteColor(color: Int) {
        this.color = color
        polylineOptions.color(color)
        updateMaps()
    }

    private fun updateMaps() {
        // Reset polylines list
        polylines = mutableListOf()
        // Add route to every map in gmaps list
        for (i in 0 until gmaps.size) {
            addPolylineToMap(gmaps[i], i)
        }
    }

    private fun addPolylineToMap(map : GoogleMap, i : Int) {
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

    fun encodePolyline() : String {
        return PolyUtil.encode(cords)
    }

    fun addCords(newCords: MutableList<LatLng>) {
        lastId++
        cords.addAll(newCords)
        polylineOptions.addAll(newCords)
        removePolyFromAllMaps(gmaps)
        updateMaps()
    }



    fun removeAllMarkers() {
        for (marker in markers) {
            marker.remove()
        }
        markers = mutableListOf()
    }

    fun addMarkerAt(pos: LatLng, text: String) {
        removeAllMarkers()
        lastMarkerPos = pos
        for (map in gmaps) {
            markers.add(map.addMarker(MarkerOptions().position(LatLng(pos.latitude, pos.longitude)).title("Here I am!"))!!)
        }
    }

    fun removeCordsAt(index: Int) {
        Log.i("RemovedCord", cords.removeAt(index).toString())

        Log.i("---", cords.size.toString())
        for (cord in cords) {
            Log.i("Cords", cords.toString())
        }
        Log.i("---", "-----")
        reloadPolylineOptions()
    }

    fun reloadPolylineOptions() {
        removePolyFromAllMaps(gmaps)
        polylineOptions = PolylineOptions()
        polylineOptions.addAll(cords)
        polylineOptions.color(color)
        updateMaps()
    }

    fun hexColor() : String {
        val color = Integer.toHexString(color).uppercase()

        return "0x" + color.substring(2) + color.substring(0,2)
    }

    fun removePolyFromMap(map: GoogleMap): Boolean {
        if (!gmaps.contains(map)) return false
        val index = gmaps.indexOf(map)
        // Possible issues: remove() might remove the polyline data too
        polylines[index].remove()
        return true
    }

    fun removePolyFromAllMaps(maps: MutableList<GoogleMap>) {
        for (map in maps) {
            removePolyFromMap(map)
        }
    }

    /**
     * Removes a Route/Polyline from a map, which removes the map from the gmap collection.
     *
     * @return false if the route isn't on the specified map.
     */
    fun removeMap(map : GoogleMap) : Boolean {
        removePolyFromMap(map)
        gmaps.remove(map)
        return true
    }

    fun removeAllMaps(maps: MutableList<GoogleMap>) {
        for (map in maps) {
            removeMap(map)
        }
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

    fun getStaticMapUrl(mapType: String, API_KEY: String): String {
        val color = hexColor()
        val width = "1000"
        val height = "515"
        val encodedPoly = encodePolyline()

        val url =
            "https://maps.google.com/maps/api/staticmap?maptype=$mapType" +
                    "&size=${width}x$height&path=color:$color%7Cweight:3%7Cenc:$encodedPoly" +
                    "&sensor=true&scale=2&key=$API_KEY"
        Log.i(TAG, color)
        return url
    }
}
