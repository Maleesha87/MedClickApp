package com.example.medclick

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * This activity is launched when the user successfully resets their password
 * via the Firebase web page and is redirected back to the app via a deep link.
 */
class ResetSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_success)

        val startButton = findViewById<MaterialButton>(R.id.btn_let_s_start)

        startButton.setOnClickListener {
            // After successful password reset, the user needs to sign in with the new password.
            navigateToSignIn()
        }
    }

    /**
     * Navigates the user to the Sign In screen, clearing the history.
     */
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            // Clear the activity stack so the user lands cleanly on Sign In
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}