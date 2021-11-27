package com.iesperemaria.djessyczaplicki.proyectorutas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityUserEditBinding

class UserEditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserEditBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private val usernames = mutableListOf<String>()
    private var username: String? = null // old username
    private var user: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        user = auth.currentUser
        binding.emailTextView.text = user?.email

        loadData()
        enableClickListeners()
    }

    private fun loadData() {
        db.collection("users")
            .get().addOnSuccessListener { users ->
                for (user in users) {
                    val username = user.getString("username") ?: "default"
                    usernames.add(username)
                    if (user.id == auth.currentUser!!.email)
                        this.username = username
                }
            }
        db.collection("users").document(user?.email.toString())
            .get().addOnSuccessListener {
                binding.usernameEditText.setText(it.get("username") as String?)
                binding.nameEditText.setText(it.get("name") as String?)
                binding.surname1EditText.setText(it.get("surname") as String?)
                binding.surname2EditText.setText(it.get("surname2") as String?)
            }
    }

    private fun enableClickListeners() {
        binding.saveButton.setOnClickListener{
            val uUsername = binding.usernameEditText.text.toString() // new username
            // Check if the username is the same as the old one, or if the uUsername is valid
            if (uUsername != username && (uUsername.isBlank() || checkUsername(uUsername)))
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.invalid_username), Toast.LENGTH_SHORT
                ).show()
            if (uUsername.length < 6 || uUsername.length > 20)
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.wrong_username_length), Toast.LENGTH_SHORT
                ).show()
            db.collection("users").document(user?.email.toString())
                .set(
                    hashMapOf(
                        "username" to uUsername,
                        "name" to binding.nameEditText.text.toString(),
                        "surname" to binding.surname1EditText.text.toString(),
                        "surname2" to binding.surname2EditText.text.toString()
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Guardado.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                }
        }
    }

    private fun checkUsername(username: String): Boolean {
        return usernames.any { it.lowercase() == username.lowercase() }
    }


}