package com.example.medclick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Data Models, Adapter, and Activity consolidated into a single file for simplicity.
 */

// --- DATA MODELS ---
interface ProviderItem { val id: String; val name: String; val rating: Float }

data class Doctor(
    override val id: String,
    override val name: String,
    val specialty: String,
    override val rating: Float,
    val imageUrl: String
) : ProviderItem

data class Hospital(
    override val id: String,
    override val name: String,
    val location: String,
    override val rating: Float,
    val imageUrl: String
) : ProviderItem

// --- MOCK DATA ---
object MockData {
    val doctors = listOf(
        Doctor("d1", "Jayasooriya", "Cardiologist", 4.7f, "url_jayasuriya"),
        Doctor("d2", "Mallikarachchi", "Neurologist", 4.8f, "url_mallika"),
        Doctor("d3", "Cruz", "Pediatrician", 4.5f, "url_cruz"),
        Doctor("d4", "Fernando", "Dermatologist", 4.6f, "url_fernando"),
        Doctor("d5", "Perera", "Cardiologist", 4.9f, "url_perera")
    )

    val hospitals = listOf(
        Hospital("h1", "Matara General", "Matara", 4.6f, "url_matara"),
        Hospital("h2", "Asiri Hospital", "Colombo", 4.9f, "url_asiri"),
        Hospital("h3", "Hemas Hospital", "Galle", 4.7f, "url_hemas"),
        Hospital("h4", "Lanka Hospitals", "Colombo", 4.8f, "url_lanka"),
        Hospital("h5", "Nawaloka Hospital", "Colombo", 4.5f, "url_nawaloka")
    )
}

// --- RECYCLERVIEW ADAPTER ---
class ProviderAdapter<T : ProviderItem>(
    private var items: List<T>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder>() {

    class ProviderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_provider_name)
        val specialtyLocation: TextView = view.findViewById(R.id.tv_provider_specialty_location)
        val rating: TextView = view.findViewById(R.id.tv_provider_rating)
        val card: CardView = view.findViewById(R.id.card_provider)
        val photo: ImageView = view.findViewById(R.id.iv_provider_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_provider_card, parent, false)
        return ProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = if (item is Doctor) "Dr. ${item.name}" else item.name
        holder.rating.text = item.rating.toString()

        // Differentiating between Doctor and Hospital details
        if (item is Doctor) {
            holder.specialtyLocation.text = item.specialty
            // Set doctor placeholder image
            holder.photo.setImageResource(R.drawable.ic_doctor_placeholder)
        } else if (item is Hospital) {
            holder.specialtyLocation.text = item.location
            // Set hospital placeholder image
            holder.photo.setImageResource(R.drawable.ic_hospital_placeholder)
        }

        holder.card.setOnClickListener { onItemClicked(item.id) }
    }

    override fun getItemCount() = items.size

    // Function to update filtered data
    fun updateData(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }
}

// --- HOME ACTIVITY ---
class HomeActivity : AppCompatActivity() {

    companion object {
        const val EMERGENCY_PHONE_NUMBER = "911"
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnCallAmbulanceMain: MaterialButton
    private lateinit var rvTopDoctors: RecyclerView
    private lateinit var rvTopHospitals: RecyclerView
    private lateinit var etSearch: TextInputEditText

    private lateinit var doctorAdapter: ProviderAdapter<Doctor>
    private lateinit var hospitalAdapter: ProviderAdapter<Hospital>

    private var allDoctors = listOf<Doctor>()
    private var allHospitals = listOf<Hospital>()
    private var filteredDoctors = listOf<Doctor>()
    private var filteredHospitals = listOf<Hospital>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeViews()
        setupListeners()
        setupRecyclerViews()
        setupSearchFunctionality()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        btnCallAmbulanceMain = findViewById(R.id.btn_call_ambulance_main)
        rvTopDoctors = findViewById(R.id.rv_top_doctors)
        rvTopHospitals = findViewById(R.id.rv_top_hospitals)
        etSearch = findViewById(R.id.et_search_placeholder)

        bottomNavigationView.selectedItemId = R.id.nav_home
        setServiceGridData()

        // Initialize data
        allDoctors = MockData.doctors
        allHospitals = MockData.hospitals
        filteredDoctors = allDoctors
        filteredHospitals = allHospitals
    }

    private fun setServiceGridData() {
        setServiceItem(R.id.service_ambulance_grid_item, "Ambulance", R.drawable.ic_ambulance)
        setServiceItem(R.id.service_doctor_grid_item, "Doctor", R.drawable.ic_doctor)
        setServiceItem(R.id.service_hospital_grid_item, "Hospitals", R.drawable.ic_hospital)
        setServiceItem(R.id.service_chat_grid_item, "Health Chat", R.drawable.ic_caregiver)
    }

    private fun setServiceItem(itemId: Int, name: String, iconResId: Int) {
        val item = findViewById<LinearLayout>(itemId)
        item.findViewById<TextView>(R.id.tv_service_name).text = name
        item.findViewById<ImageView>(R.id.iv_service_icon).setImageResource(iconResId)
    }

    private fun setupSearchFunctionality() {
        // Make search field focusable and clickable
        etSearch.isFocusable = true
        etSearch.isClickable = true

        // Search when user types
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Toast.makeText(this, "Type to search for doctors or hospitals", Toast.LENGTH_SHORT).show()
            }
        }

        // Real-time search as user types
        etSearch.setOnKeyListener { _, _, _ ->
            performSearch(etSearch.text.toString())
            false
        }
    }

    private fun setupListeners() {
        // --- BOTTOM NAVIGATION HANDLER ---
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Clear search when returning to home
                    etSearch.setText("")
                    performSearch("")
                    true
                }
                R.id.nav_doctor -> {
                    // Navigate to doctor search with current search term
                    val searchTerm = etSearch.text.toString()
                    if (searchTerm.isNotEmpty()) {
                        Toast.makeText(this, "Searching doctors for: $searchTerm", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Doctor Search - Type to filter", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_ambulance -> {
                    // NAVIGATE TO AMBULANCE ACTIVITY
                    val intent = Intent(this, AmbulanceActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true // Change to true to highlight the nav item
                }
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

        // --- HOME SCREEN SHORTCUTS ---
        btnCallAmbulanceMain.setOnClickListener { performOneTapCall() }

        findViewById<LinearLayout>(R.id.service_doctor_grid_item).setOnClickListener {
            navigateToSearchWithFilter("Doctor")
        }
        findViewById<LinearLayout>(R.id.service_hospital_grid_item).setOnClickListener {
            navigateToSearchWithFilter("Hospital")
        }
        findViewById<LinearLayout>(R.id.service_ambulance_grid_item).setOnClickListener {
            performOneTapCall()
        }
        findViewById<LinearLayout>(R.id.service_chat_grid_item).setOnClickListener {
            Toast.makeText(this, "Opening Health Chat...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSearch(query: String) {
        val searchTerm = query.trim().lowercase()

        if (searchTerm.isEmpty()) {
            // Show all data when search is empty
            filteredDoctors = allDoctors
            filteredHospitals = allHospitals
        } else {
            // Filter doctors
            filteredDoctors = allDoctors.filter { doctor ->
                doctor.name.lowercase().contains(searchTerm) ||
                        doctor.specialty.lowercase().contains(searchTerm) ||
                        "dr. ${doctor.name}".lowercase().contains(searchTerm)
            }

            // Filter hospitals
            filteredHospitals = allHospitals.filter { hospital ->
                hospital.name.lowercase().contains(searchTerm) ||
                        hospital.location.lowercase().contains(searchTerm)
            }

            // Show search results
            Toast.makeText(this, "Found ${filteredDoctors.size} doctors, ${filteredHospitals.size} hospitals", Toast.LENGTH_SHORT).show()
        }

        // Update adapters
        doctorAdapter.updateData(filteredDoctors)
        hospitalAdapter.updateData(filteredHospitals)

        // Show/hide sections based on results
        updateSectionVisibility()
    }

    // FIXED: Corrected the ClassCastException
    private fun updateSectionVisibility() {
        // Get the parent LinearLayout of each RecyclerView
        val doctorsParent = rvTopDoctors.parent as? LinearLayout
        val hospitalsParent = rvTopHospitals.parent as? LinearLayout

        // Show/hide doctors section
        doctorsParent?.let { parent ->
            if (filteredDoctors.isEmpty() && etSearch.text.toString().isNotEmpty()) {
                parent.visibility = View.GONE
            } else {
                parent.visibility = View.VISIBLE
            }
        }

        // Show/hide hospitals section
        hospitalsParent?.let { parent ->
            if (filteredHospitals.isEmpty() && etSearch.text.toString().isNotEmpty()) {
                parent.visibility = View.GONE
            } else {
                parent.visibility = View.VISIBLE
            }
        }
    }

    private fun performOneTapCall() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$EMERGENCY_PHONE_NUMBER")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: Cannot initiate call.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSearchWithFilter(category: String) {
        // Set search term based on category
        etSearch.setText(category)
        performSearch(category)
        bottomNavigationView.selectedItemId = R.id.nav_doctor
        Toast.makeText(this, "Searching for: $category", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerViews() {
        // Top Doctors
        rvTopDoctors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        doctorAdapter = ProviderAdapter(filteredDoctors) { providerId ->
            navigateToProviderProfile(providerId)
        }
        rvTopDoctors.adapter = doctorAdapter

        // Top Hospitals
        rvTopHospitals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hospitalAdapter = ProviderAdapter(filteredHospitals) { providerId ->
            navigateToProviderProfile(providerId)
        }
        rvTopHospitals.adapter = hospitalAdapter
    }

    private fun navigateToProviderProfile(providerId: String) {
        Toast.makeText(this, "Viewing Profile for ID: $providerId. (Review button will be inside)", Toast.LENGTH_LONG).show()
    }
}