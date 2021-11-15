package com.iesperemaria.djessyczaplicki.proyectorutas

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.GoogleMap
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
    private lateinit var binding: ActivityDynmapBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var routeId: String
    private lateinit var postId: String
    private lateinit var mapFrag: MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        routeId = intent.getStringExtra("route_id")!!
        postId = intent.getStringExtra("post_id")!!
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
            route = Route(name = name,color = color,newCords = cords, id = id)
            createMapFragment(route)
            if (routeOwner == auth.currentUser?.email)
                showDeleteBtn()
        }

    }

    private fun enableEventListeners() {
        binding.mapTypeHyb.setOnClickListener {
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        binding.mapTypeMap.setOnClickListener {
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        binding.mapTypeSat.setOnClickListener {
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    private fun showDeleteBtn() {
        binding.deleteButton.isVisible = true
        binding.deleteButton.setOnClickListener {
            class CustomDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return activity?.let {
                        val builder = AlertDialog.Builder(it)
                        builder.setMessage(R.string.dialog_delete_post)
                            .setPositiveButton(R.string.delete
                            ) { _, _ ->
                                deletePost()
                            }
                            .setNegativeButton(R.string.cancel, null)
                        // Create the AlertDialog object and return it
                        builder.create()
                    } ?: throw IllegalStateException("Activity cannot be null")
                }
            }

            CustomDialogFragment().show(supportFragmentManager, "DynmapActivity-showDeleteBtn()")
        }
    }

    private fun deletePost() {
        db.collection("posts").document(postId).delete()
        db.collection("routes").document(routeId).delete()
        onBackPressed()
    }

    private fun createMapFragment(route : Route) {
        val mapFragment = this.supportFragmentManager
            .findFragmentById(binding.newPostMapFragment.id) as SupportMapFragment
        mapFrag = MapsFragment(this)
        mapFragment.getMapAsync(mapFrag)
        mapFrag.addRoute(route)
        enableEventListeners()
    }
}