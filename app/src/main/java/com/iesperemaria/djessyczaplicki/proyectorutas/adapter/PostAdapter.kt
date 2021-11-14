package com.iesperemaria.djessyczaplicki.proyectorutas.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.iesperemaria.djessyczaplicki.proyectorutas.DynmapActivity
import com.iesperemaria.djessyczaplicki.proyectorutas.R
import com.iesperemaria.djessyczaplicki.proyectorutas.animation.BounceInterpolator
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.PostItemBinding
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



    class ViewHolder(
        private val binding : PostItemBinding,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        val TAG = "PostAdapterViewHolder"
        fun bind( postItem: Post ) {
            with(binding) {
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()


                checkLikeState(postItem, auth, binding)
                textViewRouteName.text = postItem.route.name
                textViewUser.setText("from: ${postItem.ownerUsername}")
                Glide.with(mContext).load(getStaticMapUrl(postItem.route)).into(mapImage)
                mapImage.setOnClickListener{
                    val intent = Intent(mContext, DynmapActivity::class.java)
                    Log.i(TAG, "onclick checked");
                    intent.putExtra("route_id", postItem.route.id)
                    mContext.startActivity(intent)
                }
                likeBtn.setOnClickListener{ toggleLike(postItem, auth, db, binding) }
//                map = postItem.mMap
//                createMapFragment(binding, postItem)


            }
        }

        private fun checkLikeState(postItem: Post, auth: FirebaseAuth, binding: PostItemBinding){

            with(binding) {
                if (postItem.likes.contains(auth.currentUser!!.email)) {
                    val animation : Animation = AnimationUtils.loadAnimation(mContext, R.anim.bounce)
                    val interpolator = BounceInterpolator(0.5, 15.0)
                    animation.interpolator = interpolator
                    likeBtn.setImageResource(R.drawable.ic_action_heart)
                    likeBtn.animation = animation
                } else {
                    val animation : Animation = AnimationUtils.loadAnimation(mContext, R.anim.bounce_reverse_back)
                    val interpolator = BounceInterpolator(0.3, 3.0)
                    animation.interpolator = interpolator
                    likeBtn.setImageResource(R.drawable.ic_action_empty_heart)
                    likeBtn.animation = animation
                }
                likeCounterTextView.text = "${postItem.likes.size} likes"
            }

        }

        private fun toggleLike(postItem: Post, auth: FirebaseAuth, db: FirebaseFirestore, binding: PostItemBinding) {
            if (postItem.likes.contains(auth.currentUser!!.email)) {
                db.collection("posts").document(postItem.id)
                    .update(
                        hashMapOf<String, Any>(
                            "likes" to FieldValue.arrayRemove(auth.currentUser!!.email)
                        )
                    )
                postItem.likes.remove(auth.currentUser!!.email)
            } else {
                db.collection("posts").document(postItem.id)
                    .update(
                        hashMapOf<String, Any>(
                            "likes" to FieldValue.arrayUnion(auth.currentUser!!.email)
                        )
                    )
                postItem.likes.add(auth.currentUser!!.email!!)
            }
            checkLikeState(postItem, auth, binding)
        }

        private fun getStaticMapUrl(route: Route): String {
            val lat = route.getMiddleCords().latitude
            val lng = route.getMiddleCords().longitude
//            val color = route.
            val width = "1000"
            val height = "515"
            val encodedPoly = route.encodePolyline(0)
            val API_KEY = mContext.getString(R.string.google_maps_key)
            val url =
                "https://maps.google.com/maps/api/staticmap?center=$lat,$lng" +
                        "&size=${width}x$height&path=weight:3%7Cenc:$encodedPoly&sensor=true&scale=2&key=$API_KEY"
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