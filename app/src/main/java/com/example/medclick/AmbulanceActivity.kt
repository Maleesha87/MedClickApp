package com.example.medclick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AmbulanceActivity : AppCompatActivity() {

    companion object {
        const val GOVERNMENT_AMBULANCE_NUMBER = "1990"
        const val EMERGENCY_NUMBER = "911" // For other emergency calls
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cardCall1990: CardView
    private lateinit var btnBookAmbulance: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ambulance)

        initializeViews()
        setupListeners()
        setupBackPressedHandler()

        // Ensure the correct item (Ambulance) is selected on the bottom nav
        bottomNavigationView.selectedItemId = R.id.nav_ambulance
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        cardCall1990 = findViewById(R.id.card_call_1990)
        btnBookAmbulance = findViewById(R.id.btn_book_ambulance)
    }

    private fun setupBackPressedHandler() {
        // Modern way to handle back button/gestures
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to HomeActivity with smooth transition
                val intent = Intent(this@AmbulanceActivity, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        })
    }

    private fun setupListeners() {

        // --- BOTTOM NAVIGATION HANDLER ---
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_doctor -> {
                    val intent = Intent(this, ProviderDashboardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_ambulance -> true // Already here, do nothing
                R.id.nav_care_giver -> {
                    Toast.makeText(this, "Care Giver services feature coming soon.", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Navigating to User Profile.", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // --- BOOK AMBULANCE BUTTON - Navigates to SelectAmbulanceTypeActivity ---
        btnBookAmbulance.setOnClickListener {
            navigateToSelectAmbulanceType()
        }

        // --- IMMEDIATE CALL ACTION (CALL-1990 card) - QUICK DIAL ---
        cardCall1990.setOnClickListener {
            performQuickDial(GOVERNMENT_AMBULANCE_NUMBER, "Government Ambulance Service")
        }

        // --- OTHER SERVICE CARDS ---
        findViewById<CardView>(R.id.card_book_ambulance).setOnClickListener {
            // Also navigate to SelectAmbulanceTypeActivity when this card is clicked
            navigateToSelectAmbulanceType()
        }

        findViewById<CardView>(R.id.card_24h_ambulance).setOnClickListener {
            // Quick dial for 24h service - you can change this number as needed
            performQuickDial(GOVERNMENT_AMBULANCE_NUMBER, "24-Hour Ambulance Service")
        }

        findViewById<CardView>(R.id.card_bulk_ambulance).setOnClickListener {
            Toast.makeText(this, "Opening Bulk Booking Form...", Toast.LENGTH_SHORT).show()
            // Implement bulk booking logic
        }

        findViewById<CardView>(R.id.card_rent_ambulance).setOnClickListener {
            Toast.makeText(this, "Opening Rental Inquiry Form...", Toast.LENGTH_SHORT).show()
            // Implement rental inquiry logic
        }

        // Read More button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_read_more).setOnClickListener {
            Toast.makeText(this, "Loading 'About Ambulance Tools' page...", Toast.LENGTH_SHORT).show()
            // You can implement a detailed information screen here
        }
    }

    /**
     * Navigates to SelectAmbulanceTypeActivity
     */
    private fun navigateToSelectAmbulanceType() {
        val intent = Intent(this, AmbulanceTypeSelectionActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Performs a quick dial to the specified number
     * This opens the dialer with the number pre-filled, user just needs to press call
     */
    private fun performQuickDial(phoneNumber: String, serviceName: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        if (intent.resolveActivity(packageManager) != null) {
            // Show confirmation toast
            Toast.makeText(this, "Dialing $serviceName: $phoneNumber", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: Cannot initiate call. No dialer app found.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Alternative method for direct calling (requires CALL_PHONE permission)
     * Note: This is more direct but requires additional permissions
     */
    private fun performDirectCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Call permission denied. Please grant call permissions.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to DIAL intent if CALL fails
            performQuickDial(phoneNumber, "Emergency Service")
        }
    }
}