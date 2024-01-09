package com.pp.coworkingapp.app.retrofit.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.response.Answer
import com.pp.coworkingapp.app.retrofit.domain.response.Review
import com.pp.coworkingapp.databinding.ItemReviewOtherBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewAdapter: ListAdapter<Review, ReviewAdapter.Holder>(ReviewAdapter.Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemReviewOtherBinding.bind(view)
        private lateinit var mainApi: MainApi
        private var listAnswer: List<Answer> = emptyList()

        fun bind(review: Review, context: Context) = with(binding) {
            if (review.photo.isNotEmpty()) {

                Picasso.get()
                    .load(review.photo)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imAvatar)
            }
            idTvName.text = review.username
            tvDateReview.text = review.createdAt.substring(0,10)
            btRatingBar.rating = review.rank.toFloat()
            tvDescReview.text = review.textReview

//            mainApi = Common.retrofitService
//
//            CoroutineScope(Dispatchers.IO).launch {
//                listAnswer = mainApi.getReviewsAnswer(review.id)
//
//                if (listAnswer.isNotEmpty()) {
//                    Log.i("Тут", "Тут1")
//                    val adapterAnswer = AnswerAdapter()
//                    binding.rcViewAnswer.layoutManager = LinearLayoutManager(context)
//                    binding.rcViewAnswer.adapter = adapterAnswer
//                    adapterAnswer.submitList(listAnswer)
//                }
//            }
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
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_other, parent, false)
        return ReviewAdapter.Holder(view)
    }

    private lateinit var context: Context

    override fun onBindViewHolder(holder: ReviewAdapter.Holder, position: Int) {
        holder.bind(getItem(position), context)
    }
}