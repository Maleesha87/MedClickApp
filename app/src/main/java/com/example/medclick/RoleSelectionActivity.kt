package com.example.medclick

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val btnGeneralUser = findViewById<MaterialButton>(R.id.btn_general_user)
        val btnServiceProvider = findViewById<MaterialButton>(R.id.btn_service_provider)
        val btnAdmin = findViewById<MaterialButton>(R.id.btn_admin)

        btnGeneralUser.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("USER_ROLE", "patient")
            startActivity(intent)
            // Don't finish() so user can go back
        }

        btnServiceProvider.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("USER_ROLE", "provider")
            startActivity(intent)
            // Don't finish() so user can go back
        }

        btnAdmin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("USER_ROLE", "admin")
            startActivity(intent)
            // Don't finish() so user can go back
        }
    }
}