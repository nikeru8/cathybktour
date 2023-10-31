package com.daniel.cathybktour.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.Image
import com.daniel.cathybktour.databinding.ImageItemBinding
import com.squareup.picasso.Picasso

class ImagePagerAdapter(private val imageList: List<Image?>?) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String) {

            if (!image.isNullOrEmpty()) {

                Picasso.get().load(image ?: "").placeholder(R.drawable.no_image).into(binding.imageView)

            } else {

                Picasso.get().load(R.drawable.no_image).into(binding.imageView)

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
        // Assuming Image has a property 'url' to hold the image url or path
        val imageUrl = imageList?.get(position)?.src
        imageUrl?.let { holder.bind(it) }

    }
}
