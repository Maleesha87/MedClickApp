package com.example.medclick

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val role = intent.getStringExtra("USER_ROLE") ?: "Admin"
        findViewById<TextView>(R.id.tv_welcome).text = "Welcome Admin!\nRole: $role"

        // Add your Admin specific UI here later
    }
}