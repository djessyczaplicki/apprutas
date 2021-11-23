package com.iesperemaria.djessyczaplicki.proyectorutas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iesperemaria.djessyczaplicki.proyectorutas.databinding.ActivityUserEditBinding

class UserEditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserEditBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        binding.emailTextView.text = user?.email

        db.collection("users").document(user?.email.toString())
            .get().addOnSuccessListener {
                binding.usernameEditText.setText(it.get("username") as String?)
                binding.nameEditText.setText(it.get("name") as String?)
                binding.surname1EditText.setText(it.get("surname") as String?)
                binding.surname2EditText.setText(it.get("surname2") as String?)
            }

        binding.saveButton.setOnClickListener{
            db.collection("users").document(user?.email.toString())
                .set(
                    hashMapOf(
                        "username" to binding.usernameEditText.text.toString(),
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

        binding.deleteButton.setOnClickListener{
            db.collection("users").document(user?.email.toString())
                .delete()

        }

    }


}