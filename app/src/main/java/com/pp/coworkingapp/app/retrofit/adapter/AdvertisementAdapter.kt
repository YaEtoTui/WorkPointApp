package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.enum.Status
import com.pp.coworkingapp.app.enum.StatusAdd
import com.pp.coworkingapp.app.retrofit.domain.response.Advertisement
import com.pp.coworkingapp.databinding.ItemPromotion3ApplicationsBinding

class AdvertisementAdapter : ListAdapter<Advertisement, AdvertisementAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemPromotion3ApplicationsBinding.bind(view)

        fun bind(advertisement: Advertisement) = with(binding) {
            binding.apply {
                tvNamePlaceCard.text = advertisement.name
                tvGeo.text = advertisement.address
                tvDateStart.text = advertisement.dateFrom

                tvStatus.text = advertisement.status
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Advertisement>() {
        override fun areItemsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion_3_applications, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}