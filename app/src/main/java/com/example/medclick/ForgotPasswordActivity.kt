package com.example.medclick

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get references for UI components
        val emailEditText = findViewById<TextInputEditText>(R.id.et_email)
        val sendResetButton = findViewById<MaterialButton>(R.id.btn_send_reset_link)
        val backToSignInLink = findViewById<TextView>(R.id.tv_back_to_sign_in)

        // --- Style the Back to Sign In link to look clickable ---
        setupLinkStyling()

        // --- 1. Send Reset Button Listener ---
        sendResetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your registered email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple requests
            sendResetButton.isEnabled = false
            sendResetButton.text = "Sending..."

            // Use Firebase to send the password reset email
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    sendResetButton.isEnabled = true
                    sendResetButton.text = "Send Reset Link"

                    if (task.isSuccessful) {
                        Log.d("PasswordReset", "Email sent to $email")
                        Toast.makeText(
                            this,
                            "Password reset link sent to $email. Check your inbox (and spam folder)!",
                            Toast.LENGTH_LONG
                        ).show()
                        // Optional: Navigate back to sign in after success
                        navigateToSignIn()
                    } else {
                        Log.w("PasswordReset", "Failed to send reset email.", task.exception)
                        Toast.makeText(
                            this,
                            "Failed to send reset email. Ensure the email is registered.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // --- 2. Back to Sign In Link Listener ---
        backToSignInLink.setOnClickListener {
            navigateToSignIn()
        }
    }

    /**
     * Makes the Back to Sign In link look like a clickable link in BLACK color
     */
    private fun setupLinkStyling() {
        val backToSignInLink = findViewById<TextView>(R.id.tv_back_to_sign_in)

        // Make the link underlined to look clickable
        backToSignInLink.paintFlags = backToSignInLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        // Keep the text color BLACK (as specified in XML)
        // No need to change the color since XML already has android:textColor="@android:color/black"
    }

    /**
     * Navigates the user back to the Sign In screen.
     */
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        // Clears the activity stack so the user can't hit back to Forgot Password
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}