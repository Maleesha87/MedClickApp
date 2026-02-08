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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database by lazy { Firebase.database }

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

        // --- Style the links to look clickable ---
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

            // Show loading state
            signInButton.isEnabled = false
            signInButton.text = "Signing In..."

            // Perform Firebase sign-in
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("SignIn", "signInWithEmail:success")
                        // Fetch user role from Realtime Database and navigate to appropriate dashboard
                        fetchUserRoleAndNavigate()
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed. Check your email and password.",
                            Toast.LENGTH_LONG).show()
                        // Reset button state
                        signInButton.isEnabled = true
                        signInButton.text = "Sign In"
                    }
                }
        }

        // --- 2. Sign Up Link Listener (Navigation) ---
        signUpLink.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            startActivity(intent)
            // Don't finish() so user can come back to sign in
        }

        // --- 3. Forgot Password Link Listener ---
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fetches user role from Realtime Database and navigates to appropriate dashboard
     */
    private fun fetchUserRoleAndNavigate() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userRef = database.getReference("users").child(user.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val role = snapshot.child("role").getValue(String::class.java) ?: "patient"
                        Log.d("SignIn", "User role: $role")
                        navigateToDashboard(role)
                    } else {
                        Log.w("SignIn", "User data not found, using default role")
                        navigateToDashboard("patient") // Default role
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("SignIn", "Error fetching user role", error.toException())
                    navigateToDashboard("patient") // Default role on error
                }
            })
        } ?: run {
            // If no user found, use default role
            navigateToDashboard("patient")
        }
    }

    /**
     * Navigates to appropriate dashboard based on user role
     */
    private fun navigateToDashboard(role: String) {
        val intent = when (role.toLowerCase()) {
            "provider", "service provider" -> Intent(this, ProviderDashboardActivity::class.java)
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            else -> Intent(this, HomeActivity::class.java) // patient or general user
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    /**
     * Makes the text views look like clickable links
     */
    private fun setupLinkStyling() {
        val signUpLink = findViewById<TextView>(R.id.tv_sign_up_link)
        val forgotPasswordLink = findViewById<TextView>(R.id.tv_forgot_password)

        signUpLink.paintFlags = signUpLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        forgotPasswordLink.paintFlags = forgotPasswordLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        val purpleColor = android.graphics.Color.parseColor("#A084E8")
        forgotPasswordLink.setTextColor(purpleColor)
    }
}