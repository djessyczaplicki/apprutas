package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val TAG = "NewUserActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding
    private val usernames = mutableListOf<String>()
    private val emails = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        loadData()

        enableEventListeners()


    }

    private fun enableEventListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.registerButton.setOnClickListener {
            val uEmail = binding.registerEmail.text.toString()
            val uPassword = binding.registerPassword.text.toString()
            val uConfirmPassword = binding.registerConfirmPassword.text.toString()
            val uUsername = binding.usernameEditText.text.toString()

            // Error checking
            if (uEmail.isBlank() || !uEmail.matches("^[a-zA-Z0-9.!#\$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$".toRegex()))
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.invalid_email), Toast.LENGTH_SHORT
                ).show()
            if (checkEmail(uEmail))
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.already_used_email), Toast.LENGTH_SHORT
                ).show()
            if (uPassword.isBlank() || uPassword.length < 6)
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.invalid_password), Toast.LENGTH_SHORT
                ).show()
            if (uPassword != uConfirmPassword)
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.non_matching_password), Toast.LENGTH_SHORT
                ).show()
            if (uUsername.isEmpty() || uUsername.isBlank() || checkUsername(uUsername))
                return@setOnClickListener Toast.makeText(
                    baseContext,
                    getString(R.string.invalid_username), Toast.LENGTH_SHORT
                ).show()

            // No errors ->
            register(uEmail, uPassword)
        }
    }

    private fun checkUsername(username: String): Boolean {
        return usernames.any { it.lowercase() == username.lowercase() }
    }

    private fun checkEmail(email: String): Boolean {
        return emails.any { it.lowercase() == email.lowercase() }
    }

    private fun loadData() {
        db.collection("users")
            .get().addOnSuccessListener { users ->
                for (user in users) {
                    emails.add(user.id)
                    usernames.add(user.getString("username") ?: "default")
                }
            }
    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext, "Registered successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    createFirestoreUserData(user!!)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Register failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun createFirestoreUserData(user: FirebaseUser) {
        db.collection("users").document(user.email.toString())
            .set(
                hashMapOf(
                    "username" to binding.usernameEditText.text.toString(),
                    "name" to binding.nameEditText.text.toString(),
                    "surname" to binding.surname1EditText.text.toString(),
                    "surname2" to binding.surname2EditText.text.toString()
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Usuario Creado.", Toast.LENGTH_SHORT).show()
                reload()
            }.addOnFailureListener {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
            }
    }

    private fun reload() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}