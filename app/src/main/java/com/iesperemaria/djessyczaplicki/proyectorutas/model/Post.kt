package com.iesperemaria.djessyczaplicki.proyectorutas.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

data class Post(
    var route : Route,
    var name : String
    ) {

    companion object {
        val data
            get() = mutableListOf(
                Post(
                    Route(cords = mutableListOf(
                        LatLng(38.641889, 0.051662),
                        LatLng(38.641914, 0.052193),
                        LatLng(38.641987, 0.053129),
                        LatLng(38.642356, 0.053853),
                        LatLng(38.641937, 0.055760)
                    )),
                    "ruta1"
                ),
                Post(
                    Route(cords = mutableListOf(
                        LatLng(38.641889, 0.051662),
                        LatLng(38.641914, 0.052193),
                        LatLng(38.641987, 0.053129),
                        LatLng(38.642356, 0.053853),
                        LatLng(38.641937, 0.055760)
                    )),
                    "ruta2"
                ),
                Post(
                    Route(cords = mutableListOf(
                        LatLng(38.641889, 0.051662),
                        LatLng(38.641914, 0.052193),
                        LatLng(38.641987, 0.053129),
                        LatLng(38.642356, 0.053853),
                        LatLng(38.641937, 0.055760)
                    )),
                    "ruta3"
                ),
                Post(
                    Route(cords = mutableListOf(
                        LatLng(38.641889, 0.051662),
                        LatLng(38.641914, 0.052193),
                        LatLng(38.641987, 0.053129),
                        LatLng(38.642356, 0.053853),
                        LatLng(38.641937, 0.055760)
                    )),
                    "ruta4"
                )
            )
    }



}