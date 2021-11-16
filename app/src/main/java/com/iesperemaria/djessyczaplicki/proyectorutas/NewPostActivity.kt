package com.iesperemaria.djessyczaplicki.proyectorutas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.CordsAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityNewPostBinding
import com.iesperemaria.djessyczaplicki.proyectorutas.fragment.MapsFragment
import java.text.SimpleDateFormat
import java.util.*

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewPostBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var mapFrag: MapsFragment
    private lateinit var cordsRecView : RecyclerView
    private lateinit var cordsAdapter: CordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        cordsRecView = binding.cordsRecView
        cordsRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        createMapFragment()
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
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
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        binding.mapTypeMap.setOnClickListener {
            mapFrag.mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        binding.mapTypeSat.setOnClickListener {
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

    fun onClickButtonSend(view : View ) {
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

    @SuppressLint("SimpleDateFormat")
    fun sendNewPost(){
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
            Toast.makeText(this,"Sent!",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Error: $it",Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickButtonAddCheckpoint(view : View) {
        mapFrag.addCheckpoint(cordsRecView.adapter!!)
    }

    fun onClickButtonAddMarker(view : View) {
        mapFrag.addMarker()
    }


}