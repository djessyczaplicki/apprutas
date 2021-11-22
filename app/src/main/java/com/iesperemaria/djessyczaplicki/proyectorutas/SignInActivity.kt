package com.iesperemaria.djessyczaplicki.proyectorutas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    private val TAG: String = "AccesoFirebase"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater);
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.signInButton.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val pwd = binding.signInPwd.text.toString()
            if (email.isBlank() || email.isEmpty() || pwd.isBlank() || pwd.isEmpty()) {
                Toast.makeText(baseContext, "Rellene los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            signIn(email, pwd)
        }

        binding.registerButton.setOnClickListener{
            // check data
            reload()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    reload()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "El correo o la contraseña es incorrecto/a",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reload() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


}