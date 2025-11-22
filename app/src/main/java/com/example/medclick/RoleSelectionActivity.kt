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
            // Navigate to General User (Patient) Dashboard
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnServiceProvider.setOnClickListener {
            // Navigate to Service Provider Dashboard
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAdmin.setOnClickListener {
            // Navigate to Admin Dashboard
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}