package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.domain.response.Answer
import com.pp.coworkingapp.databinding.ListItemAnswerReviewsBinding
import com.squareup.picasso.Picasso

class AnswerAdapter : ListAdapter<Answer, AnswerAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ListItemAnswerReviewsBinding.bind(view)

        fun bind(answer: Answer) = with(binding) {
            if (answer.userPhoto.isNotEmpty()) {

                Picasso.get()
                    .load(answer.userPhoto)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imAvatarAnswer)
            }
            idTvNameAnswer.text = answer.userName
            tvDateAnswer.text = answer.createdAt.substring(0,10)
            tvDescAnswer.text = answer.body
        }
    }

    class Comparator : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_answer_reviews, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}