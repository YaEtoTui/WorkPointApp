package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.enum.Status
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.databinding.ItemPlacesSettingsBinding

class AdapterMyPlace: ListAdapter<Place, AdapterMyPlace.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemPlacesSettingsBinding.bind(view)

        fun bind(place: Place) = with(binding) {
            tvNamePlaceCard.text = place.name
            tvGeo.text = place.address

            if (place.status.lowercase().equals(Status.UNDERREVIEW.status.lowercase())) {
                tvStatus.setText(R.string.in_processing)
                imDraw.setImageResource(R.drawable.icon_clock_list)
                view.setBackgroundResource(R.color.rect_1)
                btRedact.text = "Редактировать"
            } else if (place.status.lowercase().equals(Status.DENIED.status.lowercase())) {
                tvStatus.setText(R.string.denied)
                imDraw.setImageResource(R.drawable.icon_close_place_card)
                view.setBackgroundResource(R.color.rect_1)
                btRedact.text = "Удалить"
            } else if (place.status.lowercase().equals(Status.APPROVED.status.lowercase())) {
                tvStatus.visibility = View.INVISIBLE
                imDraw.visibility = View.INVISIBLE
                view.setBackgroundResource(R.color.rect_2)
                btRedact.text = "Редактировать"
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_places_settings, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}