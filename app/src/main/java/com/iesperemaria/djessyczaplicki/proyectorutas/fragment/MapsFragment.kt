package com.iesperemaria.djessyczaplicki.proyectorutas.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class MapsFragment(
    private val mContext : AppCompatActivity
) : SupportMapFragment(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST_CODE = 0
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private val TAG = "MapsFragment"
    private var mLocationPermissionsGranted = false
    var newRoute = Route()
    val routes : MutableList<Route> = mutableListOf()
    var mapType: String? = null

    lateinit var mMap : GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "Map is ready!")
        mMap = googleMap
//        createMarker()
        newRoute.addToMap(mMap)
        mMap.mapType = getMapType()
//        createPolylines()
        getLocationPermission()
        var wasCameraSet = false
        if (routes.isNotEmpty()) {
            Log.i(TAG, "routes is not empty" )
            wasCameraSet = reloadRoutes()
        }
        if (!wasCameraSet) {
            Log.i(TAG, "camera wasn't set" )
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 18f))
                }
        }
        routes.add(newRoute)
//        mMap.isTrafficEnabled = true
    }


    @SuppressLint("MissingPermission")
    fun addMarker() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location ->
                mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("Here I am!"))
            }

    }

    @SuppressLint("MissingPermission")
    fun addCheckpoint(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location ->
                newRoute.addCords(mutableListOf(LatLng(location.latitude, location.longitude)))
                adapter.notifyItemInserted(newRoute.cords.size-1)
                Toast.makeText(mContext, newRoute.cords.size.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun addRoute(r : Route) : Boolean {
        if (routes.contains(r)) return false
        routes.add(r)
        if (::mMap.isInitialized) reloadRoutes()
        return true
    }

    private fun getMapType(): Int {
        return when (mapType) {
            "hybrid" -> GoogleMap.MAP_TYPE_HYBRID
            "roadmap" -> GoogleMap.MAP_TYPE_NORMAL
            else -> GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    private fun reloadRoutes(): Boolean {
        // The builder is used for the camera zoom, to get the bounds of the screen that has to
        // be shown to see every route of the map
        if (routes.isEmpty()) return false
        // If all of the routes have no set cords, the builder won't work correctly
        if (routes.fold(mutableListOf<LatLng>()) { acc, elem ->
                acc.addAll(elem.cords)
                return@fold acc
            }.isEmpty()) return false

        var builder = LatLngBounds.Builder()
        for (route in routes) {
            if (route.cords.isNotEmpty()) {
                route.addToMap(mMap)
                builder = addToBoundsBuilder(route, builder)
            }
        }
        
        val bounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
        return true
    }

    /**
     * This method takes all the cords of a route, and add them to a LatLng.Builder instance
     * to determine the bounds of the route.
     */
    private fun addToBoundsBuilder(route: Route, builder: LatLngBounds.Builder) : LatLngBounds.Builder {
        for (cord in route.cords) {
            builder.include(cord)
        }
        return builder
    }

    // Location Permissions
    private fun getLocationPermission() {
        val permissions = arrayOf(FINE_LOCATION, COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(mContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(mContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true
                mMap.isMyLocationEnabled = true
            } else {
                ActivityCompat.requestPermissions(mContext, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionsGranted = false;

        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in 0..grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false
                            return
                        }
                    }
                    mLocationPermissionsGranted = true
                }
            }
        }
    }
}