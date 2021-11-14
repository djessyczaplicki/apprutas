package com.iesperemaria.djessyczaplicki.proyectorutas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityDynmapBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class DynmapActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDynmapBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val routeId = intent.getStringExtra("route_id")!!
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        var route : Route
        var routeOwner : String

        db.collection("routes").document(routeId).get().addOnSuccessListener { doc ->
            routeOwner = doc.get("user") as String? ?: "default"
            val name = doc.get("name") as String
            val color = (doc.get("color") as Long).toInt()
            val cordsList = doc.get("cords") as ArrayList<HashMap<String,Double>>
            val cords = mutableListOf<LatLng>()
            val id = doc.id
            cordsList.forEach { index ->
                cords.add(LatLng(index["latitude"]!!, index["longitude"]!!))
            }
            Log.i("DynmapActivity", id)
            route = Route(name = name,color = color,cords = cords, id = id)
            createMapFragment(route)
            if (routeOwner == auth.currentUser?.email)
                showDeleteBtn()
        }

    }

    private fun showDeleteBtn() {
        val removeBtn = Button(this)
        removeBtn.text = getText(R.string.remove_post)
        removeBtn.foregroundGravity = Gravity.END
        removeBtn.width = 70
        removeBtn.height = 30
        removeBtn.setPadding(10,0,10,0)
        binding.toolBar.addView(removeBtn)
        Snackbar.make(removeBtn, ":c", Snackbar.LENGTH_SHORT).show()
    }

    private fun createMapFragment(route : Route) {
        val mapFragment = this.supportFragmentManager
            .findFragmentById(binding.newPostMapFragment.id) as SupportMapFragment
        val mapFrag = MapsFragment(this)
        mapFragment.getMapAsync(mapFrag)
        mapFrag.addRoute(route)
    }
}