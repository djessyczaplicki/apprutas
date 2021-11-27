package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.LikeItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post

class LikesAdapter(
    val users: List<String>,
    private val mContext: Context
) : RecyclerView.Adapter<LikesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LikeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.count()

    class ViewHolder(
        private val binding : LikeItemBinding,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        val TAG = "LikesAdapterViewHolder"
        fun bind(user: String) {
            with(binding) {
                userTextView.text = user
            }
        }
    }
}