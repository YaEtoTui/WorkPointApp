package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.domain.response.Review
import com.pp.coworkingapp.databinding.ItemReviewOtherBinding
import com.squareup.picasso.Picasso

class ReviewAdapter: ListAdapter<Review, ReviewAdapter.Holder>(ReviewAdapter.Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemReviewOtherBinding.bind(view)

        fun bind(review: Review) = with(binding) {
            if (!review.photo.isEmpty()) {

                Picasso.get()
                    .load(review.photo)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imAvatar)
            }
            idTvName.text = review.username
            tvDateReview.text = review.createdAt.substring(0,10)
            btRatingBar.rating = review.rank.toFloat()
            tvDescReview.text = review.textReview
        }
    }

    class Comparator : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_other, parent, false)
        return ReviewAdapter.Holder(view)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.Holder, position: Int) {
        holder.bind(getItem(position))
    }
}