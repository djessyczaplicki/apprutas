package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.PostItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class PostAdapter(
    private var postList : MutableList<Post>,
    private val mContext : Context
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size

    class ViewHolder(private val binding : PostItemBinding, private var mContext : Context) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind( postItem: Post) {
                with(binding) {
                    textViewRouteName.text = postItem.name // postItem.route.name
                    // map = postItem.mMap
                    createMapFragment()
                }
            }


        private fun createMapFragment() {
            val mapFragment = SupportMapFragment.newInstance()

            val myContext = mContext as AppCompatActivity
            myContext.supportFragmentManager
                .beginTransaction()
                .add(binding.mapFragment.id, mapFragment)
                .commit()

            Log.i("PostAdapter", "Created a Map Fragment")
            mapFragment.getMapAsync(MapsFragment(myContext))
        }

    }


}