package com.pp.coworkingapp.app.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pp.coworkingapp.R
import com.pp.coworkingapp.databinding.FragmentAddNewPlaceCommonBinding
import com.pp.coworkingapp.databinding.ItemTextSpinnerBinding

class FilterAdapter: ListAdapter<String, FilterAdapter.Holder>(Comparator()){

    private lateinit var onButtonClickListener: OnButtonClickListener

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemTextSpinnerBinding.bind(view)

        fun bind(str: String, onButtonClickListener: OnButtonClickListener) = with(binding) {
            binding.tvText.text = str

            binding.tvText.setOnClickListener {
                onButtonClickListener.onClick(binding.tvText.text.toString())
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text_spinner, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), onButtonClickListener)
    }

    interface OnButtonClickListener {
        fun onClick(strChoice: String)
    }

    fun setOnButtonClickListener(listener: FilterAdapter.OnButtonClickListener) {
        onButtonClickListener = listener
    }
}