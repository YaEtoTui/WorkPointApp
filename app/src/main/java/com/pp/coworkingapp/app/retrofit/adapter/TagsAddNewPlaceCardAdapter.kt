package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.domain.response.Tag
import com.pp.coworkingapp.databinding.ItemTagsClickAddNewPlaceCardBinding

class TagsAddNewPlaceCardAdapter: ListAdapter<Tag, TagsAddNewPlaceCardAdapter.Holder>(TagsAddNewPlaceCardAdapter.Comparator()){

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemTagsClickAddNewPlaceCardBinding.bind(view)

        fun bind(tag: Tag) = with(binding) {
            tvTag.text = tag.name
            imCrest.visibility = View.INVISIBLE
        }
    }

    class Comparator : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsAddNewPlaceCardAdapter.Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tags_click_add_new_place_card, parent, false)
        return TagsAddNewPlaceCardAdapter.Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}