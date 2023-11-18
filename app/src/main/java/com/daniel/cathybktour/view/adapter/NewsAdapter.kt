package com.daniel.cathybktour.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.databinding.ItemNewsBinding
import com.daniel.cathybktour.model.NewsData

class NewsAdapter(private var list: MutableList<NewsData>, private val itemClick: (NewsData) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {


    inner class NewsViewHolder(var binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: NewsData) {

            binding.tvNewsTitle.text = model.title
            binding.tvNewsDescription.text = model.description
            binding.root.setOnClickListener {

                itemClick(model)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        val itemView = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(itemView)

    }

    override fun getItemCount(): Int = list.size ?: 0

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        holder.bind(list[position])

    }


}