package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val TAG = "NewUserActivity"
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        binding.registerButton.setOnClickListener {
            val uEmail = binding.registerEmail.text.toString()
            val uPassword = binding.registerPassword.text.toString()
            val uConfirmPassword = binding.registerConfirmPassword.text.toString()
            if (uEmail.isNotEmpty() && uEmail.isNotBlank()){
                if (uPassword == uConfirmPassword)
                    register(uEmail, uPassword)
                else
                    Toast.makeText(baseContext, "Passwords are diferent", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(baseContext, "Invalid Email", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun register(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Registered successfully.",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Register failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


}