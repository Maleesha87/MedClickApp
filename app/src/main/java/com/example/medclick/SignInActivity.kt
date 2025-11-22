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

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get references for UI components
        val emailEditText = findViewById<TextInputEditText>(R.id.et_email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.et_password)
        val signInButton = findViewById<MaterialButton>(R.id.btn_sign_in)
        val signUpLink = findViewById<TextView>(R.id.tv_sign_up_link)
        val forgotPasswordLink = findViewById<TextView>(R.id.tv_forgot_password)

        // --- Style the links to look clickable (ADD THIS) ---
        setupLinkStyling()

        // --- Pre-fill email if passed from SignUpActivity ---
        intent.getStringExtra("EMAIL")?.let { email ->
            emailEditText.setText(email)
            passwordEditText.requestFocus()
        }

        // --- 1. Sign In Button Listener ---
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform Firebase sign-in
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("SignIn", "signInWithEmail:success")
                        navigateToDashboard()
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed. Check your email and password.",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }

        // --- 2. Sign Up Link Listener (Navigation) ---
        signUpLink.setOnClickListener {
            // Navigate the user back to the Sign Up screen
            val intent = Intent(this, RoleSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- 3. Forgot Password Link Listener (Placeholder) ---
        forgotPasswordLink.setOnClickListener {
            Toast.makeText(this, "Forgot Password functionality coming soon.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Makes the text views look like clickable links
     */
    private fun setupLinkStyling() {
        val signUpLink = findViewById<TextView>(R.id.tv_sign_up_link)
        val forgotPasswordLink = findViewById<TextView>(R.id.tv_forgot_password)

        // Make both links underlined to look clickable
        signUpLink.paintFlags = signUpLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        forgotPasswordLink.paintFlags = forgotPasswordLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        // Optional: Change color to make them stand out more
        val purpleColor = android.graphics.Color.parseColor("#A084E8") // Your purple color
        forgotPasswordLink.setTextColor(purpleColor)

    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}