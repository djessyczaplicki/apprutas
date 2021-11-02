package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class PostAdapter( private val routeList : MutableList<Route>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    }
}