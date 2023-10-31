package com.daniel.cathybktour.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.ItemLanguageBinding
import com.daniel.cathybktour.model.Language

class LanguageAdapter(private val languages: List<Language>, private val itemClick: (Language) -> Unit) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Language) {
            binding.language = model
            if (model.isSelected) {

                binding.root.setBackgroundResource(R.drawable.main_solid_radius_10)
                binding.textView.setTextColor(Color.WHITE)

            } else {

                binding.root.setBackgroundColor(Color.WHITE)
                binding.textView.setTextColor(Color.BLACK)

            }
            binding.root.setOnClickListener {

                languages.forEach { it.isSelected = false }
                model.isSelected = true
                itemClick(model)
                notifyDataSetChanged()

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = languages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language)

    }
}
