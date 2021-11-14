package com.iesperemaria.djessyczaplicki.proyectorutas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.PostAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityMainBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import com.iesperemaria.djessyczaplicki.proyectorutas.model.User


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postRecView : RecyclerView
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
                isGranted: Boolean ->
            if(isGranted) {
                Log.i("Permission: " , "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    override fun onBackPressed() {
        Toast.makeText(this, "You are already on the main menu!", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        enableClickEventListeners()
        // Recycler View
        postRecView = binding.postRecView
        postRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
    }

    private fun enableClickEventListeners() {
        binding.logoutMenuBtn.setOnClickListener { signOut() }
        binding.newPostMenuBtn.setOnClickListener {
            startActivity(Intent(this, NewPostActivity::class.java))
        }
        binding.userMenuBtn.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        requestPermission()
        generatePosts()
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this,SignInActivity::class.java))
        finish()
    }

    private fun generatePosts(){
        val posts = mutableListOf<Post>()
        val routes = mutableListOf<Route>()
        val users = mutableListOf<User>()
        db.collection("routes")
            .get().addOnSuccessListener { routesColl ->
                for (doc in routesColl.documents) {
                    val name = doc.get("name") as String
                    val color = (doc.get("color") as Long).toInt()
                    val cordsList = doc.get("cords") as ArrayList<HashMap<String,Double>>
                    val cords = mutableListOf<LatLng>()
                    cordsList.forEach { index ->
                        cords.add(LatLng(index["latitude"]!!, index["longitude"]!!))
                    }
                    routes.add(Route(name = name,color = color,cords = cords, id = doc.id))
                    Log.i(TAG, "routes set")
                }
                // get posts
                db.collection("posts")
                    .get().addOnSuccessListener { postsColl ->
                        for (doc in postsColl.documents) {
                            Log.i(TAG, "And another one!")
                            val routeId = doc.getString("route")
                            val owner = doc.getString("owner") ?: "default"
                            val likes = doc.get("likes") as MutableList<String>
                            posts.add(Post(doc.id, routes.first{it.id == routeId}, owner, owner, likes))
                        }
                        Log.i(TAG, "posts set")

                        // get users
                        db.collection("users")
                            .get().addOnSuccessListener { usersColl ->
                                for (doc in usersColl.documents) {
                                    val email = doc.id
                                    val address = doc.getString("address")!!
                                    val birthday = doc.getString("birthday")!!
                                    val name = doc.getString("name")!!
                                    val phone = doc.getString("phone")!!
                                    val surname = doc.getString("surname")!!
                                    val surname2 = doc.getString("surname2")!!
                                    val username = doc.getString("username")!!
                                    users.add(User(email, address, birthday, name, phone, surname, surname2, username))
                                }
                                Log.i(TAG, "users set")

                                for (post in posts) {
                                    // Search for the owner in the users list, if the user isn't in the list, the value doesn't change
                                    post.ownerUsername = users.find{it.email == post.owner}?.username ?: post.ownerUsername
                                }

                                binding.loadingPanel.visibility = View.GONE
                                val postAdapter = PostAdapter(posts, this)
                                Log.i(TAG, "all set¿?" + posts.size)

                                postRecView.adapter = postAdapter
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this, R.string.loading_error, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, R.string.loading_error, Toast.LENGTH_LONG).show()
            }
    }


    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                FINE_LOCATION
            ) -> {
                // additional rationale should be displayed

                Snackbar.make(
                    binding.root,"Please accept the location permissions in order to use the app", Snackbar.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(
                    FINE_LOCATION
                )
            }
            else -> {
                requestPermissionLauncher.launch(
                    FINE_LOCATION
                )
            }
        }
    }

}