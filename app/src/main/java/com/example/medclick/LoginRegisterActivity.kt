package com.example.medclick

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity presenting the user with Sign In and Sign Up options.
 * This screen follows the Onboarding flow.
 */
class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        findViewById<Button>(R.id.btn_sign_in).setOnClickListener {
            navigateToSignInForm()
        }

        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {
            navigateToRoleSelection()
        }
    }

    /**
     * Handles the process for new users (Sign Up) by guiding them to role selection.
     */
    private fun navigateToRoleSelection() {
        Toast.makeText(this, "New User: Select your role to continue.", Toast.LENGTH_LONG).show()

        startActivity(Intent(this, RoleSelectionActivity::class.java))

        finish()
    }

    /**
     * Handles the process for existing users (Sign In).
     */
    private fun navigateToSignInForm() {
        Toast.makeText(this, "Existing User: Navigating to Sign In Form.", Toast.LENGTH_LONG).show()

        startActivity(Intent(this, SignInActivity ::class.java))
        finish()
    }
}