package com.iesperemaria.djessyczaplicki.proyectorutas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.PostAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityMainBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Query
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import com.iesperemaria.djessyczaplicki.proyectorutas.model.User


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postRecView: RecyclerView
    private var mapType = "roadmap"
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        enableEventListeners()
        // Recycler View
        postRecView = binding.postRecView
        postRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.swipeRefresh.isRefreshing = true

        generatePosts()
    }

    private fun enableEventListeners() {
        binding.swipeRefresh.setOnRefreshListener { generatePosts() }
        // Method to enable and disable the swipeRefresh
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.postRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.swipeRefresh.isEnabled =
                        (verticalOffset == 0 && binding.postRecView.computeVerticalScrollOffset() == 0)
                }
            })
        })
        binding.swipeRefresh.setProgressViewOffset(true, 130, 200)
        binding.logoutMenuBtn.setOnClickListener { signOut() }
        binding.mapTypeButton.setOnClickListener {
            binding.swipeRefresh.isRefreshing = true
            val mapTypes = listOf("roadmap", "hybrid", "satellite")
            val newMapTypeIndex = (mapTypes.indexOf(mapType) + 1) % mapTypes.size
            mapType = mapTypes[newMapTypeIndex]
            db.collection("users").document(auth.currentUser!!.email!!)
                .update(
                    hashMapOf(
                        "mapType" to mapType
                    ) as Map<String, Any>
                ).addOnSuccessListener {
                    generatePosts()
                }

        }
        binding.newPostMenuBtn.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("map_type", mapType)
            startActivity(intent)
        }
        binding.userMenuBtn.setOnClickListener {
            startActivity(Intent(this, UserEditActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        requestPermission()
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun generatePosts() {
        val posts = mutableListOf<Post>()
        val routes = mutableListOf<Route>()
        val users = mutableListOf<User>()
        db.collection("routes")
            .get().addOnSuccessListener { routesColl ->
                for (doc in routesColl.documents) {
                    val name = doc.getString("name") ?: "default"
                    val color = (doc.get("color") as Long).toInt()
                    val cordsList = doc.get("cords") as ArrayList<HashMap<String, Double>>
                    val cords = mutableListOf<LatLng>()
                    cordsList.forEach { index ->
                        cords.add(LatLng(index["latitude"]!!, index["longitude"]!!))
                    }
                    val length = doc.getString("length") ?: "0:00"
                    routes.add(Route(name = name, color = color, newCords = cords, id = doc.id, length = length))
                }
                Log.i(TAG, "routes set")
                // get posts
                db.collection("posts").orderBy("date", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener { postsColl ->
                        for (doc in postsColl.documents) {
                            val routeId = doc.getString("route")
                            val owner = doc.getString("owner") ?: "default"
                            val likes = doc.get("likes") as MutableList<String>
                            val date = doc.getString("date") ?: "10/10/2021"

                            posts.add(
                                Post(
                                    doc.id,
                                    routes.first { it.id == routeId },
                                    owner,
                                    owner,
                                    likes,
                                    date
                                )
                            )
                        }
                        Log.i(TAG, "posts set")

                        // get users
                        db.collection("users")
                            .get().addOnSuccessListener { usersColl ->
                                for (doc in usersColl.documents) {
                                    val email = doc.id
                                    val username = doc.getString("username") ?: ""
                                    val name = doc.getString("name") ?: ""
                                    val mapType = doc.get("mapType") as String? ?: "roadmap"
                                    users.add(
                                        User(
                                            email,
                                            username,
                                            name,
                                            mapType
                                        )
                                    )
                                }
                                Log.i(TAG, "users set")

                                for (post in posts) {
                                    // Search for the owner in the users list, if the user isn't in the list, the value doesn't change
                                    post.ownerUsername =
                                        users.find { it.email == post.owner }?.username
                                            ?: post.ownerUsername
                                }
                                mapType =
                                    users.find { it.email == auth.currentUser!!.email }?.mapType ?: "roadmap"
                                binding.swipeRefresh.isRefreshing = false
                                val postAdapter = PostAdapter(posts, this, mapType)

                                postRecView.adapter = postAdapter
                            }

                    }.addOnFailureListener {
                        binding.swipeRefresh.isRefreshing = false
                        Toast.makeText(this, R.string.loading_error, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                binding.swipeRefresh.isRefreshing = false
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
                    binding.root,
                    "Please accept the location permissions in order to use the app",
                    Snackbar.LENGTH_LONG
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