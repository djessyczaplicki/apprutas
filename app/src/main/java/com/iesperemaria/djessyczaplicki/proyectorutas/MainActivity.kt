package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.PostAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityMainBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val primaryNavigationFragment = Fragment()
    private lateinit var auth : FirebaseAuth
    override fun onBackPressed() {
        Toast.makeText(this, "You are already on the main menu!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val user = auth.currentUser
        binding.signOutButton.setOnClickListener {signOut()}
        binding.userButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
        val postRecView : RecyclerView = binding.postRecView
        postRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        val postAdapter = PostAdapter(Post.data, this, primaryNavigationFragment)

        postRecView.adapter = postAdapter
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this,SignInActivity::class.java))
    }

    fun onClickButtonNewPost(view: android.view.View) {
        startActivity(Intent(this, NewPostActivity::class.java))
    }




}