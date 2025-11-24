package com.example.medclick

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AmbulanceTypeAdapter(
    private val types: List<AmbulanceType>,
    private val onTypeSelected: (AmbulanceType) -> Unit
) : RecyclerView.Adapter<AmbulanceTypeAdapter.TypeViewHolder>() {

    class TypeViewHolder(val view: CardView) : RecyclerView.ViewHolder(view) {
        val typeName: TextView = view.findViewById(R.id.tv_type_name)
        val typeCost: TextView = view.findViewById(R.id.tv_type_cost)
        val availability: TextView = view.findViewById(R.id.tv_availability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ambulance_type_card, parent, false) as CardView
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val type = types[position]

        holder.typeName.text = type.name
        holder.typeCost.text = type.cost

        val availabilityText = if (type.isAvailable) "Available" else "Not available"
        val availabilityColor = if (type.isAvailable) R.color.med_green else R.color.med_red

        holder.availability.text = availabilityText
        holder.availability.setTextColor(holder.view.context.getColor(availabilityColor))

        // Set up click listener for selection
        holder.view.setOnClickListener {
            if (type.isAvailable) {
                onTypeSelected(type)
            } else {
                Toast.makeText(holder.view.context, "${type.name} is currently unavailable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = types.size
}