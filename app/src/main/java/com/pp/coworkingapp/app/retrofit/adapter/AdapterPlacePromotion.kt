package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.databinding.ItemPromotionSettingsBinding

class AdapterPlacePromotion: ListAdapter<Place, AdapterPlacePromotion.Holder>(Comparator()) {

    private lateinit var onButtonClickListener: OnButtonClickListener

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemPromotionSettingsBinding.bind(view)

        fun bind(place: Place, onButtonClickListener: OnButtonClickListener) = with(binding) {
            tvNamePlaceCard.text = place.name
            tvCity.text = place.city
            tvGeo.text = place.address

            btPromote.setOnClickListener {
                onButtonClickListener.onClick(place.id)
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
            .inflate(R.layout.item_promotion_settings, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), onButtonClickListener)
    }

    interface OnButtonClickListener {
        fun onClick(placeId: Int)
    }

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        onButtonClickListener = listener
    }
}