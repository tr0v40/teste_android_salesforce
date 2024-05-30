package com.example.sallesforce

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter(private val items: List<String>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.carousel_text)
        val layout: ConstraintLayout = itemView.findViewById(R.id.carousel_card_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carousel_card, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.textView.text = items[position]
        val backgroundColor = when (position % 4) {
            0 -> Color.RED
            1 -> Color.GREEN
            2 -> Color.BLUE
            else -> Color.YELLOW
        }
        holder.layout.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount() = items.size
}
