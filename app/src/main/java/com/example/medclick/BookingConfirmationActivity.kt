package com.example.medclick

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BookingConfirmationActivity : AppCompatActivity() {

    // Firebase - Realtime Database
    private lateinit var database: FirebaseDatabase
    private lateinit var bookingsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // Views
    private lateinit var btnBack: ImageView
    private lateinit var tvSelectedType: TextView
    private lateinit var tvEstimatedCost: TextView
    private lateinit var etPickupAddress: EditText
    private lateinit var etDestinationAddress: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnBookNow: MaterialButton

    // Data passed from previous activity
    private var defaultPickupAddress: String = ""
    private var defaultDestinationAddress: String = ""
    private lateinit var selectedType: AmbulanceType

    // Date validation
    private val calendar = Calendar.getInstance()
    private val currentTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        // 1. Initialize Firebase
        initializeFirebase()

        // 2. Extract data passed from the selection screen
        extractIntentData()

        // 3. Initialize UI views
        initializeViews()

        // 4. Set up content based on the data
        displayBookingDetails()

        // 5. Set up date/time picker logic
        setupDateTimePickers()

        // 6. Set up listeners (Back and Book Now)
        setupListeners()
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: IllegalStateException) {
            // Already initialized, proceed
        }

        // Initialize Realtime Database (NOT Firestore)
        database = FirebaseDatabase.getInstance()
        bookingsRef = database.getReference("ambulance_bookings")
        auth = Firebase.auth

        // Check if user is properly authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to book an ambulance", Toast.LENGTH_LONG).show()
            redirectToLogin()
            return
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun extractIntentData() {
        defaultPickupAddress = intent.getStringExtra("EXTRA_PICKUP") ?: ""
        defaultDestinationAddress = intent.getStringExtra("EXTRA_DESTINATION") ?: ""

        // Using modern, type-safe approach for Serializable data
        selectedType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_AMBULANCE_TYPE", AmbulanceType::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_AMBULANCE_TYPE") as? AmbulanceType
        } ?: AmbulanceType("Standard Service", "Rs 0", true)
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        tvSelectedType = findViewById(R.id.tv_selected_type)
        etPickupAddress = findViewById(R.id.et_pickup_address)
        etDestinationAddress = findViewById(R.id.et_destination_address)
        etDate = findViewById(R.id.et_date)
        etTime = findViewById(R.id.et_time)
        etNotes = findViewById(R.id.et_notes)
        btnBookNow = findViewById(R.id.btn_book_now)
    }

    private fun displayBookingDetails() {
        tvSelectedType.text = selectedType.name

        // Set default addresses from previous screen, but allow editing
        etPickupAddress.setText(defaultPickupAddress)
        etDestinationAddress.setText(defaultDestinationAddress)

        // Set default date/time to current time
        val now = Calendar.getInstance()
        etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now.time))
        etTime.setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(now.time))
    }

    private fun setupDateTimePickers() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Date Picker Dialog - restrict to current and future dates only
        etDate.setOnClickListener {
            val currentCalendar = Calendar.getInstance()
            val year = currentCalendar.get(Calendar.YEAR)
            val month = currentCalendar.get(Calendar.MONTH)
            val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                // Validate if selected date is not in the past
                if (selectedCalendar.timeInMillis >= currentCalendar.timeInMillis) {
                    etDate.setText(dateFormat.format(selectedCalendar.time))
                } else {
                    Toast.makeText(this, "Please select a current or future date", Toast.LENGTH_SHORT).show()
                }
            }, year, month, day)

            // Set minimum date to today
            datePicker.datePicker.minDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        // Time Picker Dialog
        etTime.setOnClickListener {
            val currentCalendar = Calendar.getInstance()
            val hour = currentCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = currentCalendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }

                // If date is today, validate that time is not in the past
                val selectedDate = etDate.text.toString()
                val today = dateFormat.format(currentCalendar.time)

                if (selectedDate == today && selectedTime.timeInMillis < currentCalendar.timeInMillis) {
                    Toast.makeText(this, "Please select a future time for today", Toast.LENGTH_SHORT).show()
                } else {
                    etTime.setText(timeFormat.format(selectedTime.time))
                }
            }, hour, minute, true).show() // true for 24-hour format
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish() // Go back to the selection screen
        }

        btnBookNow.setOnClickListener {
            handleBookingSubmission()
        }
    }

    private fun handleBookingSubmission() {
        val currentUser = auth.currentUser

        // Ensure user is properly authenticated
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to book an ambulance", Toast.LENGTH_LONG).show()
            redirectToLogin()
            return
        }

        val pickupAddress = etPickupAddress.text.toString().trim()
        val destinationAddress = etDestinationAddress.text.toString().trim()
        val bookingDate = etDate.text.toString().trim()
        val bookingTime = etTime.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        // Validate required fields
        if (pickupAddress.isEmpty()) {
            etPickupAddress.error = "Please enter pickup address"
            return
        }

        if (destinationAddress.isEmpty()) {
            etDestinationAddress.error = "Please enter destination address"
            return
        }

        if (bookingDate.isEmpty() || bookingTime.isEmpty()) {
            Toast.makeText(this, "Please select both date and time", Toast.LENGTH_LONG).show()
            return
        }

        // Validate date is not in the past
        val selectedDateTime = "$bookingDate $bookingTime"
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        try {
            val selectedTimeMillis = dateTimeFormat.parse(selectedDateTime)?.time ?: 0
            if (selectedTimeMillis < currentTime) {
                Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_LONG).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_LONG).show()
            return
        }

        // Show loading state
        btnBookNow.isEnabled = false
        btnBookNow.text = "Booking..."

        val userId = currentUser.uid
        val currentTimestamp = System.currentTimeMillis()

        // Create the booking data with user's UID and additional details
        val booking = hashMapOf(
            "userId" to userId,
            "userEmail" to (currentUser.email ?: "unknown"),
            "userPhone" to (currentUser.phoneNumber ?: "unknown"),
            "pickupAddress" to pickupAddress,
            "destinationAddress" to destinationAddress,
            "ambulanceType" to selectedType.name,
            "estimatedCost" to selectedType.cost,
            "scheduleDate" to bookingDate,
            "scheduleTime" to bookingTime,
            "scheduleDateTime" to selectedDateTime,
            "additionalNotes" to notes,
            "status" to "pending", // pending, confirmed, in_progress, completed, cancelled
            "createdAt" to currentTimestamp,
            "updatedAt" to currentTimestamp
        )

        // Save to Realtime Database (NOT Firestore)
        val bookingKey = bookingsRef.push().key // Generate unique key

        if (bookingKey != null) {
            bookingsRef.child(bookingKey).setValue(booking)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking confirmed! Reference: $bookingKey", Toast.LENGTH_LONG).show()
                    navigateToSuccessScreen(bookingKey, pickupAddress, destinationAddress)
                }
                .addOnFailureListener { e ->
                    println("Error adding booking: $e")
                    Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
                    // Reset button state
                    btnBookNow.isEnabled = true
                    btnBookNow.text = "Book Now"
                }
        } else {
            Toast.makeText(this, "Booking failed: Could not generate booking ID", Toast.LENGTH_LONG).show()
            btnBookNow.isEnabled = true
            btnBookNow.text = "Book Now"
        }
    }

    private fun navigateToSuccessScreen(bookingId: String, pickupAddress: String, destinationAddress: String) {
        val intent = Intent(this, BookingSuccessActivity::class.java).apply {
            putExtra("BOOKING_ID", bookingId)
            putExtra("AMBULANCE_TYPE", selectedType.name)
            putExtra("ESTIMATED_COST", selectedType.cost)
            putExtra("PICKUP_ADDRESS", pickupAddress)
            putExtra("DESTINATION_ADDRESS", destinationAddress)
        }
        startActivity(intent)
        finish()
    }
}