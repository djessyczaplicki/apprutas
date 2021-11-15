package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.CordsItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.PostItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class CordsAdapter(
    val route: Route,
    private val mContext: Context
) : RecyclerView.Adapter<CordsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CordsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(route, position)
    }

    override fun getItemCount(): Int = route.cords.size

    class ViewHolder(
        private val binding : CordsItemBinding,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var adapter: CordsAdapter

        fun bind(route: Route, index: Int) {
            with(binding) {
                remove.setOnClickListener{
                    adapter.route.removeCordsAt(index)
                    adapter.notifyItemRemoved(adapterPosition)
                }
            }
        }
    }
}