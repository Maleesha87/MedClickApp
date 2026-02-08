package com.example.medclick

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProviderDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_dashboard)

        val role = intent.getStringExtra("USER_ROLE") ?: "Service Provider"
        findViewById<TextView>(R.id.tv_welcome).text = "Welcome Service Provider!\nRole: $role"

        // Add your Service Provider specific UI here later
    }
}