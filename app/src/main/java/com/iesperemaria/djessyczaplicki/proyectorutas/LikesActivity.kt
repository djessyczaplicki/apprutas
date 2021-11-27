package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.iesperemaria.djessyczaplicki.proyectorutas.adapter.LikesAdapter
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityLikesBinding

class LikesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLikesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var postId: String
    private lateinit var likesRecView: RecyclerView
    private lateinit var likesAdapter: LikesAdapter
    private val usersWhoLiked = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        likesRecView = binding.likesRecView
        postId = intent.getStringExtra("postId") ?: ""
        db = FirebaseFirestore.getInstance()
        binding.logo.setOnClickListener { goToMainMenu() }
        getUsersWhoLiked()
    }

    private fun getUsersWhoLiked() {
        db.collection("posts").document(postId)
            .get().addOnSuccessListener { doc ->
                val usersEmailWhoLiked = doc.get("likes") as MutableList<String>
                val orderedUsersEmailWhoLiked = mutableListOf<String>()

                db.collection("users")
                    .get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (usersEmailWhoLiked.any{ it == document.id }) {
                                usersWhoLiked.add(document.getString("username") ?: "default")
                                orderedUsersEmailWhoLiked.add(document.id)
                            }
                        }

                        likesAdapter = LikesAdapter(usersWhoLiked, orderedUsersEmailWhoLiked, this)
                        likesRecView.adapter = likesAdapter
                    }
            }
    }

    private fun goToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}