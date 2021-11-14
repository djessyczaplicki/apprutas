package com.iesperemaria.djessyczaplicki.proyectorutas.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

data class Post(
    val id : String,
    var route : Route,
    var owner : String,
    var ownerUsername : String,
    val likes : MutableList<String> = mutableListOf()
    )