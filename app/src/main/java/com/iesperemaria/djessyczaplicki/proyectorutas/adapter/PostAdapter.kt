package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.PostItemBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Post
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route

class PostAdapter(
    private var postList : MutableList<Post>,
    private val mActivity : AppCompatActivity,
    private val primaryNavigationFragment: Fragment
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size



    class ViewHolder(
        private val binding : PostItemBinding,
        private val mActivity: AppCompatActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        val TAG = "PostAdapterViewHolder"
        fun bind( postItem: Post ) {
            with(binding) {
                textViewRouteName.text = postItem.name // postItem.route.name

                Glide.with(mActivity).load(getStaticMapUrl(postItem.route)).into(mapImage)
                // map = postItem.mMap
//                createMapFragment(binding, postItem)

            }
        }

        private fun getStaticMapUrl(route: Route): String {
            val middleIndex : Int = route.cords.size/2
            val middleCords = route.cords[middleIndex]
            val lat = middleCords.latitude
            val lng = middleCords.longitude
            val width = "1000"
            val height = "515"
            val API_KEY = mActivity.getString(R.string.google_maps_key)
            val url =
                "https://maps.google.com/maps/api/staticmap?center=$lat,$lng" +
                        "&zoom=15&size=${width}x$height&sensor=false&key=$API_KEY"
            Log.i(TAG, url)
            return url
        }


//        private fun createMapFragment(binding: PostItemBinding, postItem: Post) {
//            Log.i("PostAdapter", "Creating a Map Fragment...")
//            val supportMapFragment = SupportMapFragment.newInstance()
//            val myContext = mContext as AppCompatActivity
//            myContext.supportFragmentManager
//                .beginTransaction()
//                .add(binding.mapFragment.id, supportMapFragment, binding.textViewRouteName.text.toString())
//                .commit()
//
//            Log.i("PostAdapter", "Created a Map Fragment")
//            val mapFragment = MapsFragment(myContext)
//            supportMapFragment.getMapAsync(mapFragment)
//            mapFragment.addRoute(postItem.route)
//        }

//        private fun createMapFragment(binding: PostItemBinding, postItem: Post) {
////            val option = GoogleMapOptions()
////                .liteMode(true)
//            val mapFragment = mActivity.supportFragmentManager
//                .findFragmentById(R.id.postItemMapFragment) as SupportMapFragment
//            mapFragment.getMapAsync(MapsFragment(mActivity))
//        }

    }


}