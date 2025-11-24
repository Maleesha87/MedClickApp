package com.example.medclick

import java.io.Serializable

data class AmbulanceType(
    val name: String,
    val cost: String,
    val isAvailable: Boolean = true,
    val iconResId: Int = R.drawable.ic_ambulance_cross
) : Serializable