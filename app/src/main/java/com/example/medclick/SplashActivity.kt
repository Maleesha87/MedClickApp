package com.example.medclick

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity responsible for showing the application's splash screen.
 */
class SplashActivity : AppCompatActivity() {

    // Define the duration for the splash screen (3 seconds)
    private val SPLASH_DELAY_MS = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge implementation compatible with API 24+
        setupEdgeToEdge()

        setContentView(R.layout.activity_splash)

        // Handle system bars for edge-to-edge
        setupWindowInsetsListener()

        // Use a coroutine launched in the lifecycle scope to manage the delay
        // and transition safely, even if the user navigates away quickly.
        lifecycleScope.launch {
            delay(SPLASH_DELAY_MS) // Wait for 3 seconds

            // Create an intent to navigate to the onboarding activity
            val intent = Intent(this@SplashActivity, OnboardingActivity::class.java)
            startActivity(intent)

            // Finish the splash activity so the user cannot navigate back to it
            finish()
        }
    }

    private fun setupEdgeToEdge() {
        // For API 30+ use modern approach, for older versions use flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(false)
        } else {
            // Android 10 and below (API 29-)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }

    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}