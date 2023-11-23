package com.pp.coworkingapp.app.retrofit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.databinding.ListItemPlacesBinding

class PlaceAdapter: ListAdapter<Place, PlaceAdapter.Holder>(Comparator()) {

    private lateinit var onButtonClickListener: OnButtonClickListener

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ListItemPlacesBinding.bind(view)

        fun bind(place: Place, onButtonClickListener: OnButtonClickListener) = with(binding) {
            //
            tvHello.text = place.name
            if (place.description.length > 200) {
                tvTextDesc.text = String.format("%s...", place.description.substring(0, 200))
            } else {
                tvTextDesc.text = place.description
            }

            Glide.with(itemView.context)
                .load(place.photo)
                .transform(RoundedCorners(100))
                .error(R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(idRectanglePhoto)

//            if (!place.photo.isEmpty()) {
//                Picasso.get()
//                    .load(place.photo)
//                    .centerCrop().fit()
//                    .transform(RoundedCornerTreatment(15f))
//                    .error(R.drawable.ic_launcher_foreground)
//                    .into(idRectanglePhoto)
//            }

            tvRating.text = place.rating
            tvGeo.text = place.address
            tvTime.text = place.openingHours
            btShowInCarte.setOnClickListener {
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
            .inflate(R.layout.list_item_places, parent, false)
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