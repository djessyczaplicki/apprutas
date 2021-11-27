package com.iesperemaria.djessyczaplicki.proyectorutas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.CordsAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityNewPostBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.PopupNewPostBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import com.iesperemaria.djessyczaplicki.proyectorutas.model.Route
import java.text.SimpleDateFormat
import java.util.*
import yuku.ambilwarna.AmbilWarnaDialog






class NewPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var mapFrag: MapsFragment
    private lateinit var cordsRecView: RecyclerView
    private lateinit var cordsAdapter: CordsAdapter
    private var mapType = "roadmap"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        mapType = intent.getStringExtra("map_type")!!

        cordsRecView = binding.cordsRecView
        cordsRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        createMapFragment()
        mapFrag.mapType = mapType
    }

    private var simpleCallback =
        object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val startPosition = viewHolder.adapterPosition
                val endPosition = target.adapterPosition

                Collections.swap(mapFrag.newRoute.cords, startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
                mapFrag.newRoute.reloadPolylineOptions()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

        }

    private fun setupRecyclerView() {
        cordsAdapter = CordsAdapter(mapFrag.newRoute, this)
        cordsRecView.adapter = cordsAdapter
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(cordsRecView)
    }

    private fun enableEventListeners() {
        binding.mapTypeHyb.setOnClickListener {
            mapType = "hybrid"
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        binding.mapTypeMap.setOnClickListener {
            mapType = "roadmap"
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        binding.mapTypeSat.setOnClickListener {
            mapType = "satellite"
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }


    private fun createMapFragment() {
        val mapFragment = this.supportFragmentManager
            .findFragmentById(binding.newPostMapFragment.id) as SupportMapFragment
        mapFrag = MapsFragment(this)
        mapFragment.getMapAsync(mapFrag)
        setupRecyclerView()
        enableEventListeners()
    }

    // show popup_new_post
    fun onClickButtonSend(v: View) {
        val route = mapFrag.newRoute
        if (route.cords.isEmpty()) return Toast.makeText(this, getString(R.string.no_route_error), Toast.LENGTH_SHORT).show()
        val popupBinding = PopupNewPostBinding.inflate(layoutInflater)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val orientation = resources.configuration.orientation
        val popupWindow = PopupWindow(
            popupBinding.root,
            width * orientation,
            height,
            true
        ) // multiplying to make it bigger (not professional)

        popupWindow.enterTransition = Slide()
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        popupWindow.exitTransition = Fade()
        // set the text values if they're not equal to the default values
        if (route.name != Route.DEFAULT_NAME)
            popupBinding.routeName.setText(route.name)
        if (route.length != Route.DEFAULT_LENGTH)
            popupBinding.routeLength.setText(route.length)
        Glide.with(this).load(route.getStaticMapUrl(mapType, getString(R.string.google_maps_key))).into(popupBinding.mapImage)
        // popupBinding.routeColor.backgroundTintList = ColorStateList.valueOf(route.color)



        // to preserve the route values
        popupWindow.setOnDismissListener {
            route.name = popupBinding.routeName.text.toString()
            route.length = popupBinding.routeLength.text.toString()
        }

        popupBinding.routeColor.setOnClickListener { // btnRouteColor ->
            AmbilWarnaDialog(this, route.color, object :
                AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    // btnRouteColor.backgroundTintList = ColorStateList.valueOf(color)
                    route.setRouteColor(color)
                    Glide.with(this@NewPostActivity).load(route.getStaticMapUrl(mapType, getString(R.string.google_maps_key))).into(popupBinding.mapImage)
                }

                override fun onCancel(dialog: AmbilWarnaDialog) {
                }
            }
            ).show()
        }
        popupBinding.send.setOnClickListener {
            route.name = popupBinding.routeName.text.toString()
            route.length = popupBinding.routeLength.text.toString()

            class CustomDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return activity?.let {
                        val builder = AlertDialog.Builder(it)
                        builder.setMessage(R.string.dialog_create_post)
                            .setPositiveButton(R.string.confirm
                            ) { _, _ ->
                                sendNewPost()
                            }
                            .setNegativeButton(R.string.cancel, null)
                        // Create the AlertDialog object and return it
                        builder.create()
                    } ?: throw IllegalStateException("Activity cannot be null")
                }
            }


            CustomDialogFragment().show(supportFragmentManager, "NewPostActivity-ConfirmCreate")
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sendNewPost() {
        val currentTime = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.format(currentTime.time)

        val route = mapFrag.newRoute
        val routeDoc = db.collection("routes").document()
        routeDoc.set(
            hashMapOf(
                "name" to route.name,
                "color" to route.color,
                "cords" to route.cords,
                "length" to route.length,
                "user" to auth.currentUser!!.email
            )
        ).addOnSuccessListener {
            db.collection("posts").document()
                .set(
                    hashMapOf(
                        "likes" to listOf<String>(),
                        "owner" to auth.currentUser!!.email,
                        //"ownerUsername" to auth.currentUser.username
                        "route" to routeDoc.id,
                        "date" to date
                    )
                )
            Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickButtonAddCheckpoint(view: View) {
        mapFrag.addCheckpoint(cordsRecView.adapter!!)
    }


}