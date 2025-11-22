package com.example.medclick

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val role = intent.getStringExtra("USER_ROLE") ?: "General User"
        findViewById<TextView>(R.id.tv_welcome).text = "Welcome General User!\nRole: $role"

        // Add your General User specific UI here later
    }
}