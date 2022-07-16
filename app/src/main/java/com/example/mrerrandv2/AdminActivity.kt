package com.example.mrerrandv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mrerrandv2.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Admin"

        binding.riderlist.setOnClickListener {
            val intent = Intent(this, RiderListActivity::class.java)
            startActivity(intent)
        }

        binding.userlist.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<Button>(R.id.logoutButton)
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this@AdminActivity, "Signed out", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@AdminActivity, SignInActivity::class.java))
            finish()
        }

    }
}