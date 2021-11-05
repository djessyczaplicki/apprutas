package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.PostAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityMainBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import android.view.MotionEvent
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postRecView : RecyclerView = binding.postRecView
        postRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        val postAdapter = PostAdapter(Post.data, this)

        postRecView.adapter = postAdapter
    }



    fun onClickButtonNewPost(view: android.view.View) {
        startActivity(Intent(this, NewPostActivity::class.java))
    }


}