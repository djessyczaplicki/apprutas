package com.iesperemaria.djessyczaplicki.proyectorutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    private lateinit var binding : ActivityMainBinding
    private var route : Route? = null

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createFragment()

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        createMarker()
        createPolylines()
        enableLocation()
//        mMap.isTrafficEnabled = true
    }


    private fun createPolylines() {
//        val polylineOptions = PolylineOptions()
//            .add(LatLng(38.641889, 0.051662))
//            .add(LatLng(38.641914, 0.052193))
//            .add(LatLng(38.641987, 0.053129))
//            .add(LatLng(38.642356, 0.053853))
//            .add(LatLng(38.641937, 0.055760))
//            .color(R.color.old_rose)
//
//        val polyline = mMap.addPolyline(polylineOptions)
//
//        polyline.startCap = RoundCap()
//        polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow2))


        val listaCords = mutableListOf(
            LatLng(38.641889, 0.051662),
            LatLng(38.641914, 0.052193),
            LatLng(38.641987, 0.053129),
            LatLng(38.642356, 0.053853),
            LatLng(38.641937, 0.055760)
        )

        val route = Route(this, mMap, cords = listaCords)
        route.addToMap()
        route.addCordToRoute(mutableListOf(LatLng(38.642050, 0.059450)))
        route.setRouteColor(getColor(R.color.old_rose))

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

        val route2 = Route(this, mMap, cords = listaCords2, color = getColor(R.color.black))
        route2.addToMap()
//        route.removeFromMap()
    }

    @SuppressLint("MissingPermission")
    private fun createMarker() {
        // Add a marker in Sydney and move the camera
        val benidorm = LatLng(38.534168, -0.131389)
        mMap.addMarker(MarkerOptions().position(benidorm).title("Marker in Benidorm"))
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(benidorm, 15f),
            3000,
            null
        )

    }

    @SuppressLint("MissingPermission")
    fun addMarker(view: View) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                    location : Location -> mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("Here I am!"))
            }

    }

    @SuppressLint("MissingPermission")
    fun addCheckpoint(view: View) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                    location : Location ->
                run {
                    if (route == null) {
                        route = Route(this, mMap, cords = mutableListOf(LatLng(location.latitude, location.longitude)))
                    } else {
                        route!!.addCordToRoute(mutableListOf(LatLng(location.latitude, location.longitude)))
                    }
                }
            }

    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::mMap.isInitialized) return
        if (isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización, ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::mMap.isInitialized) return
        if (!isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "Para activar la localización, ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

}