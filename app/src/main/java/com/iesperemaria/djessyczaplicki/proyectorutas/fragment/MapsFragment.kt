package com.iesperemaria.djessyczaplicki.proyectorutas.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.iesperemaria.djessyczaplicki.proyectorutas.NewPostActivity
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class MapsFragment(
    val mContext : Context
) : Fragment(), OnMapReadyCallback {

    private var route : Route? = null
    lateinit var mMap : GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        createMarker()
        Log.i("MapsFragment", "Map is ready!")
        createPolylines()
//        enableLocation()

//        mMap.isTrafficEnabled = true
    }

//    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
//        super.onInflate(context, attrs, savedInstanceState)
//        if (this.isAdded()) {
//            Toast.makeText(activity, "hola", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun createPolylines() {

        val listaCords = mutableListOf(
            LatLng(38.641889, 0.051662),
            LatLng(38.641914, 0.052193),
            LatLng(38.641987, 0.053129),
            LatLng(38.642356, 0.053853),
            LatLng(38.641937, 0.055760)
        )

        val route = Route( mMap, cords = listaCords) // requireContext(),
        route.addToMap()
        route.addCordToRoute(mutableListOf(LatLng(38.642050, 0.059450)))
        route.setRouteColor(mContext.getColor(R.color.old_rose))

        val listaCords2 = mutableListOf(
            LatLng(38.554139465112, -0.12599945068359375),
            LatLng(38.55309907775221, -0.1297760009765625),
            LatLng(38.552964833125216, -0.13629913330078125),
            LatLng(38.5521593600993, -0.14110565185546875),
            LatLng(38.54960863598394, -0.1480579376220703),
            LatLng(38.54692356547259, -0.15488147735595703),
            LatLng(38.54353352075651, -0.1622629165649414),
            LatLng(38.53729004998249, -0.1719188690185547),
            LatLng(38.53668581437226, -0.17286300659179688),
            LatLng(38.53802855328022, -0.17354965209960938),
            LatLng(38.53839780208588, -0.17406463623046875),
            LatLng(38.53856144583678, -0.17405927181243896),
            LatLng(38.53859920972644, -0.17468154430389404),
            LatLng(38.538494309984, -0.17587780952453613)
        )

        val route2 = Route(mMap, cords = listaCords2, color = mContext.getColor(R.color.black))
        route2.addToMap()
//        route.removeFromMap()
    }

    @SuppressLint("MissingPermission")
    private fun createMarker() {
        // Add a marker in benidorm and move the camera
        val benidorm = LatLng(38.534168, -0.131389)
        mMap.addMarker(MarkerOptions().position(benidorm).title("Marker in Benidorm"))
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(benidorm, 15f),
            3000,
            null
        )

    }

    @SuppressLint("MissingPermission")
    fun addMarker() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
                    if (route == null) {
                        route = Route(mMap, cords = mutableListOf(LatLng(location.latitude, location.longitude))) // requireContext(),
                    } else {
                        route!!.addCordToRoute(mutableListOf(LatLng(location.latitude, location.longitude)))
                    }
                }
            }

    }

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