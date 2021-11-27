package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.LikesAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.PostAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityLikesBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityUserProfileBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import com.iesperemaria.djessyczaplicki.proyectorutas.model.User

class UserProfileActivity : AppCompatActivity() {
    private val TAG = "UserProfileActivity"
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var postRecView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var profileOwnerEmail: String
    private lateinit var profileOwner: User
    private lateinit var mapType: String
    private var posts = mutableListOf<Post>()
    private val usersWhoLiked = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        profileOwnerEmail = intent.getStringExtra("profile_owner") ?: auth.currentUser!!.email!!
        binding.swipeRefresh.isRefreshing = true
        postRecView = binding.postRecView
        postRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        generatePosts()
        enableEventListeners()
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
        binding.editProfileBtn.setOnClickListener { startActivity(Intent(this, UserEditActivity::class.java)) }
        binding.logo.setOnClickListener { goToMainMenu() }
    }

    private fun generatePosts() {
        val routes = mutableListOf<Route>()
        posts = mutableListOf()
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
                    routes.add(
                        Route(
                            name = name,
                            color = color,
                            newCords = cords,
                            id = doc.id,
                            length = length
                        )
                    )
                }
                Log.i(TAG, "routes set")
                db.collection("posts").orderBy("date", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener { postColl ->
                        for (post in postColl.documents) {
                            val owner = post.getString("owner") ?: "default"
                            if (profileOwnerEmail == owner) {
                                val date = post.getString("date") ?: "10/10/2021"
                                val likes = post.get("likes") as MutableList<String>
                                val routeId = post.get("route")
                                posts.add(
                                    Post(
                                        post.id,
                                        routes.first { it.id == routeId },
                                        owner,
                                        owner,
                                        likes,
                                        date
                                    )
                                )
                            }
                        }
                        Log.i(TAG, "posts set")
                        getOwnerData()
                    }
            }.addOnFailureListener {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this, R.string.loading_error, Toast.LENGTH_LONG).show()
            }
    }

    private fun getOwnerData() {
        db.collection("users").document(profileOwnerEmail)
            .get().addOnSuccessListener { doc ->
                val email = doc.id
                val username = doc.getString("username") ?: ""
                val name = doc.getString("name") ?: ""
                val surname = doc.getString("surname") ?: ""
                val surname2 = doc.getString("surname2") ?: ""
                val mapType = doc.get("mapType") as String? ?: "roadmap"

                profileOwner = User(
                        email,
                        username,
                        name,
                        surname,
                        surname2,
                        mapType
                    )
                db.collection("users").document(auth.currentUser!!.email!!)
                    .get().addOnSuccessListener {

                        this.mapType = it.getString("mapType") ?: "roadmap"
                        posts.forEach { it.ownerUsername = profileOwner.username }
                        loadRecView()
                        setOwnerData()
                    }
            }
    }

    private fun loadRecView() {
        val postAdapter = PostAdapter(posts, this, mapType)
        postRecView.adapter = postAdapter
    }

    private fun setOwnerData() {
        with(binding) {
            username.text = profileOwner.username
            email.text = profileOwner.email
            name.text = profileOwner.name
            surname.text = profileOwner.surname
            surname2.text = profileOwner.surname2
            swipeRefresh.isRefreshing = false
            if (auth.currentUser!!.email == profileOwnerEmail)
                editProfileBtn.visibility = View.VISIBLE
        }
    }

    fun goToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}