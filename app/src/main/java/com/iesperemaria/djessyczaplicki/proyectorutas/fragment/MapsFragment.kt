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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var mLocationPermissionsGranted = false
    lateinit var newRoute : Route
    val routes : MutableList<Route> = mutableListOf()

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
        mMap = googleMap
//        createMarker()
        newRoute = Route(mMap)
        routes.add(newRoute)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        Log.i("MapsFragment", "Map is ready!")
//        createPolylines()
        if (routes.isNotEmpty()) {
            reloadRoutes()
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 18f))
                }
        }
        getLocationPermission()
//        mMap.isTrafficEnabled = true
    }


    @SuppressLint("MissingPermission")
    fun addMarker() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                    location : Location -> mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("Here I am!"))
            }

    }

    @SuppressLint("MissingPermission")
    fun addCheckpoint() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                    location : Location ->
                run {
                    newRoute.addCords(mutableListOf(LatLng(location.latitude, location.longitude)))
                }
            }

    }

    fun addRoute(r : Route) : Boolean {
        if (routes.contains(r)) return false
        routes.add(r)
        if (::mMap.isInitialized) reloadRoutes()
        return true
    }


    private fun reloadRoutes() {
        // The builder is used for the camera zoom, to get the bounds of the screen that has to
        // be shown to see every route of the map
        if (routes.isEmpty()) return
        var builder = LatLngBounds.Builder()
        for (route in routes) {
            route.addToMap(mMap)
            builder = addToBoundsBuilder(route, builder)
        }
        val bounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
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

//    private fun createPolylines() {
//
//        val listaCords = mutableListOf(
//            LatLng(38.641889, 0.051662),
//            LatLng(38.641914, 0.052193),
//            LatLng(38.641987, 0.053129),
//            LatLng(38.642356, 0.053853),
//            LatLng(38.641937, 0.055760)
//        )
//
//        val route = Route( mMap, newCords = listaCords) // requireContext(),
//        route.addToMap(mMap)
//        route.addCordToRoute(mutableListOf(LatLng(38.642050, 0.059450)))
//        route.setRouteColor(mContext.getColor(R.color.old_rose))
//
//        val listaCords2 = mutableListOf(
//            LatLng(38.554139465112, -0.12599945068359375),
//            LatLng(38.55309907775221, -0.1297760009765625),
//            LatLng(38.552964833125216, -0.13629913330078125),
//            LatLng(38.5521593600993, -0.14110565185546875),
//            LatLng(38.54960863598394, -0.1480579376220703),
//            LatLng(38.54692356547259, -0.15488147735595703),
//            LatLng(38.54353352075651, -0.1622629165649414),
//            LatLng(38.53729004998249, -0.1719188690185547),
//            LatLng(38.53668581437226, -0.17286300659179688),
//            LatLng(38.53802855328022, -0.17354965209960938),
//            LatLng(38.53839780208588, -0.17406463623046875),
//            LatLng(38.53856144583678, -0.17405927181243896),
//            LatLng(38.53859920972644, -0.17468154430389404),
//            LatLng(38.538494309984, -0.17587780952453613)
//        )
//
//        val route2 = Route(mMap, newCords = listaCords2, color = mContext.getColor(R.color.black))
//        route2.addToMap(mMap)
////        route.removeFromMap()
//    }



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

/////////////////////////////////// old ->

//    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
//        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED
//
//    @SuppressLint("MissingPermission")
//    private fun enableLocation() {
//        if (!::mMap.isInitialized) return
//        if (isLocationPermissionGranted()) {
//            mMap.isMyLocationEnabled = true
//        } else {
//            requestLocationPermission()
//        }
//    }

//    private fun requestLocationPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                requireActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        ) {
//            Toast.makeText(requireContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
//        } else {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                NewPostActivity.REQUEST_CODE_LOCATION
//            )
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            NewPostActivity.REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mMap.isMyLocationEnabled = true
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Para activar la localización, ve a ajustes y acepta los permisos",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else -> {
//            }
//        }
//    }


//
//    @SuppressLint("MissingPermission")
//    fun onResumeFragments() {
//        super.onResumeFragments()
//        if (!::mMap.isInitialized) return
//        if (!isLocationPermissionGranted()) {
//            mMap.isMyLocationEnabled = false
//            Toast.makeText(
//                requireContext(),
//                "Para activar la localización, ve a ajustes y acepta los permisos",
//                Toast.LENGTH_SHORT
//            ).show()
//
//        }
//    }
}