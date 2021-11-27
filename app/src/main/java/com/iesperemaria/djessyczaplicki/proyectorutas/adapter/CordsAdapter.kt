package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.CordsItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route


class CordsAdapter(
    val route: Route,
    private val mContext: Context
) : RecyclerView.Adapter<CordsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CordsItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding, mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(route.cords[position], this)
    }

    override fun getItemCount(): Int = route.cords.size

    class ViewHolder(
        val binding : CordsItemBinding,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(cords: LatLng, adapter: CordsAdapter) {
            with(binding) {
                latlng.text = "$cords [${adapterPosition+1}]"
                cardView.setOnClickListener {
                    if (adapter.route.lastMarkerPos == cords) {
                        adapter.route.removeAllMarkers()
                        adapter.route.lastMarkerPos = null
                    } else
                        adapter.route.addMarkerAt(cords, mContext.getString(R.string.marker_default))
                }
                remove.setOnClickListener{
                    val index = adapterPosition
                    adapter.route.removeCordsAt(index)
                    adapter.notifyItemRemoved(index)
                    Log.i("CordsAdapter", index.toString())
                }
                move.setOnClickListener {
                    Toast.makeText(mContext, mContext.getString(R.string.hold_to_move), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}