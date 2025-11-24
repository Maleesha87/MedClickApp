package com.example.medclick

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton

class BookingSuccessActivity : AppCompatActivity() {

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var tvBookingId: TextView
    private lateinit var tvServiceType: TextView
    private lateinit var tvEstimatedCost: TextView
    private lateinit var tvPickupAddress: TextView
    private lateinit var tvDestinationAddress: TextView
    private lateinit var btnBackToHome: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_success)

        initializeViews()
        displayBookingDetails()
        setupListeners()
        setupBackPressedHandler()
    }

    private fun initializeViews() {
        lottieAnimationView = findViewById(R.id.iv_success_icon)
        tvBookingId = findViewById(R.id.tv_booking_id)
        tvServiceType = findViewById(R.id.tv_service_type)
        tvEstimatedCost = findViewById(R.id.tv_estimated_cost)
        tvPickupAddress = findViewById(R.id.tv_pickup_address)
        tvDestinationAddress = findViewById(R.id.tv_destination_address)
        btnBackToHome = findViewById(R.id.btn_back_to_home)
    }

    private fun displayBookingDetails() {
        // Get data passed from BookingConfirmationActivity
        val bookingId = intent.getStringExtra("BOOKING_ID") ?: "N/A"
        val ambulanceType = intent.getStringExtra("AMBULANCE_TYPE") ?: "Unknown Service"
        val estimatedCost = intent.getStringExtra("ESTIMATED_COST") ?: "Rs 0"
        val pickupAddress = intent.getStringExtra("PICKUP_ADDRESS") ?: "Unknown location"
        val destinationAddress = intent.getStringExtra("DESTINATION_ADDRESS") ?: "Unknown destination"

        // Display the booking details
        tvBookingId.text = bookingId.take(8) // Show first 8 characters of booking ID
        tvServiceType.text = ambulanceType
        tvEstimatedCost.text = estimatedCost
        tvPickupAddress.text = pickupAddress
        tvDestinationAddress.text = destinationAddress
    }

    private fun setupListeners() {

        btnBackToHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun setupBackPressedHandler() {
        // Modern way to handle back button/gesture
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When back button/gesture is pressed, go to home screen
                navigateToHome()
            }
        }

        // Add the callback to the OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun navigateToHome() {
        // Navigate back to main home screen and clear the back stack
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}