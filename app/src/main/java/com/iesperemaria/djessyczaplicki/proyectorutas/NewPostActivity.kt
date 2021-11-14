package com.iesperemaria.djessyczaplicki.proyectorutas

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityNewPostBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import java.text.SimpleDateFormat
import java.util.*

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewPostBinding
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var mapFragment: MapsFragment
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
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

    @SuppressLint("SimpleDateFormat")
    fun onClickButtonSend(view : View ) {
        val currentTime = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.format(currentTime.time)

        val route = mapFragment.newRoute
            ?: return Toast.makeText(this, "No route created.", Toast.LENGTH_LONG).show()
        val routeDoc = db.collection("routes").document()
        routeDoc.set(
            hashMapOf(
                "name" to route.name,
                "color" to route.color,
                "cords" to route.cords,
                "user" to auth.currentUser!!.email
            )
        ).addOnSuccessListener {
            db.collection("posts").document()
                .set(
                    hashMapOf(
                        "likes" to listOf<String>(),
                        "owner" to auth.currentUser!!.email,
                        //"ownerUsername" to auth.currentUser.username
                        "route" to routeDoc.id,
                        "date" to date
                    )
                )
            Toast.makeText(this,"Sent!",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Error: $it",Toast.LENGTH_SHORT).show()
        }


    }

    fun onClickButtonAddCheckpoint(view : View) {
        mapFragment.addCheckpoint()
    }

    fun onClickButtonAddMarker(view : View) {
        mapFragment.addMarker()
    }

}