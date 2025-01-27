package com.example.bfu.adapters

import android.view.View
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.bfu.R
import com.example.bfu.models.PhoneModel

class PhonesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val phoneName: TextView = itemView.findViewById(R.id.item_title)
    private val price: TextView = itemView.findViewById(R.id.item_price)
    private val date: TextView = itemView.findViewById(R.id.item_launch_date)
    private val score: TextView = itemView.findViewById(R.id.item_camera_score)

    fun bind(mPhones: PhoneModel) {
        phoneName.text = mPhones.name
        price.text = mPhones.price
        date.text = mPhones.date
        score.text = mPhones.score
    }
}