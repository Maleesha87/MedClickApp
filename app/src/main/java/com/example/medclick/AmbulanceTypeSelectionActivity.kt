package com.example.medclick

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AmbulanceTypeSelectionActivity : AppCompatActivity() {

    private lateinit var rvAmbulanceTypes: RecyclerView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ambulance_type_selection)

        initializeViews()
        setupListeners()
        setupRecyclerView()
    }

    private fun initializeViews() {
        rvAmbulanceTypes = findViewById(R.id.rv_ambulance_types)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish() // Go back to AmbulanceActivity
        }
    }

    private fun setupRecyclerView() {
        // Mock data for ambulance types
        val mockTypes = listOf(
            AmbulanceType("Patient Transfer", "Rs 1000", true, R.drawable.ic_ambulance_van),
            AmbulanceType("Basic Life Support (BLS)", "Rs 2500", true, R.drawable.ic_ambulance_cross),
            AmbulanceType("Dead Body Transfer", "Rs 1500", true, R.drawable.ic_ambulance_cross)
        )

        rvAmbulanceTypes.apply {
            layoutManager = LinearLayoutManager(this@AmbulanceTypeSelectionActivity)
            adapter = AmbulanceTypeAdapter(mockTypes) {  selectedType ->
                navigateToBookingConfirmation(selectedType)
            }
        }
    }

    private fun navigateToBookingConfirmation(selectedType: AmbulanceType) {
        val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
            // Pass the selected ambulance type
            putExtra("EXTRA_AMBULANCE_TYPE", selectedType)
            // Note: You might want to pass actual location data here instead of hardcoded values
        }
        startActivity(intent)
    }
}