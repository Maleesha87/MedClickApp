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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get the role passed from RoleSelectionActivity
        val userRole = intent.getStringExtra("USER_ROLE") ?: "patient"
        Log.d("SignUp", "User role: $userRole")

        // Get references for UI components
        val fullNameEditText = findViewById<TextInputEditText>(R.id.et_full_name)
        val emailEditText = findViewById<TextInputEditText>(R.id.et_email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.et_password)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.et_confirm_password)
        val signUpButton = findViewById<MaterialButton>(R.id.btn_sign_up)
        val signInLink = findViewById<TextView>(R.id.tv_sign_in_link)

        // --- Style the Sign In link to look clickable ---
        setupLinkStyling()

        // --- 1. Sign Up Button Listener ---
        signUpButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Firebase Sign-Up Implementation ---
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("SignUp", "createUserWithEmail:success")

                        // Get the newly created user
                        val user = auth.currentUser
                        user?.let {
                            // Create user document in Firestore
                            createUserDocument(it.uid, email, fullName, userRole)
                        }
                    } else {
                        // Handle specific errors
                        when {
                            task.exception?.message?.contains("already in use") == true -> {
                                Toast.makeText(this, "Email already registered. Please sign in instead.", Toast.LENGTH_LONG).show()
                                // Auto-navigate to sign in with pre-filled email
                                navigateToSignIn(email)
                            }
                            else -> {
                                Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }

        // --- 2. Sign In Link Listener (Navigation) ---
        signInLink.setOnClickListener {
            // Navigate to Sign In screen
            navigateToSignIn()
        }
    }

    /**
     * Makes the Sign In link look like a clickable link
     */
    private fun setupLinkStyling() {
        val signInLink = findViewById<TextView>(R.id.tv_sign_in_link)

        // Make the link underlined to look clickable
        signInLink.paintFlags = signInLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
    }

    /**
     * Creates a user document in Firestore after successful authentication
     */
    private fun createUserDocument(userId: String, email: String, fullName: String, role: String) {
        val userData = hashMapOf(
            "email" to email,
            "fullName" to fullName,
            "role" to role,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "profileCompleted" to false,
            "phoneNumber" to "",
            "dateOfBirth" to "",
            "address" to ""
        )

        db.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User document created successfully")
                // Show success dialog after Firestore document is created
                showSuccessDialog(email)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user document", e)
                // Even if Firestore fails, the user is authenticated, so show success
                Toast.makeText(this, "Account created but there was an issue saving profile data", Toast.LENGTH_LONG).show()
                showSuccessDialog(email)
            }
    }

    /**
     * Shows the custom success dialog.
     */
    private fun showSuccessDialog(email: String) {
        val dialog = SuccessDialog(this) {
            // This is the action that runs when the "Welcome" button is clicked
            // Navigate to SignInActivity after successful registration
            navigateToSignIn(email)
        }
        dialog.show()
    }

    /**
     * Navigates to SignInActivity (with optional email pre-fill)
     */
    private fun navigateToSignIn(email: String = "") {
        val intent = Intent(this, SignInActivity::class.java).apply {
            // Pass the email to pre-fill in SignInActivity
            if (email.isNotEmpty()) {
                putExtra("EMAIL", email)
            }
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}