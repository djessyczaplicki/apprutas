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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityNewPostBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewPostBinding
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var mapFragment: MapsFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createMapFragment()
    }



    private fun createMapFragment() {
        supportMapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(binding.newPostMapFragment.id, supportMapFragment)
            .commit()

        mapFragment = MapsFragment(this)
        supportMapFragment.getMapAsync(mapFragment)
    }

    fun onClickButtonAddCheckpoint(view : View) {
        mapFragment.addCheckpoint()
    }

    fun onClickButtonAddMarker(view : View) {
        mapFragment.addMarker()
    }

}