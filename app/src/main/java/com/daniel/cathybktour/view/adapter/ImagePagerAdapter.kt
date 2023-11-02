package com.daniel.cathybktour.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.Image
import com.daniel.cathybktour.databinding.ImageItemBinding
import com.squareup.picasso.Picasso

class ImagePagerAdapter(private val imageList: MutableList<Image?>? = mutableListOf(Image(".jpg", "drawable", ""))) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String?) {

            if (!image.isNullOrEmpty()) {

                Picasso.get().load(image ?: "").placeholder(R.drawable.taipei_icon).into(binding.imageView)

            } else {

                Log.d("TAG","checkpoint sssssss")
                Picasso.get().load(R.drawable.taipei_icon).into(binding.imageView)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return imageList?.size ?: 0

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val imageUrl = imageList?.get(position)?.src

        if (imageUrl == "drawable") {

            holder.bind(null)

        } else {

            imageUrl?.let { holder.bind(it) }

        }

    }
}
