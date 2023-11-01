package com.daniel.cathybktour.view.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.RvLoadingBinding
import com.daniel.cathybktour.databinding.RvMainItemBinding
import com.squareup.picasso.Picasso


class TourAdapter(private val itemClick: (TourItem) -> Unit) :
    ListAdapter<TourItem, RecyclerView.ViewHolder>(DiffItemCallback()) {

    private var TYPE_NORMAL = 0
    private var TYPE_FOOTER = 1

    //是否顯示Footer
    private var showFooter: Boolean = true

    private var firstIn = true

    init {


    }

    inner class AttractionAdapterViewHolder(var binding: RvMainItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("CheckResult", "SetTextI18n")
        fun bind(model: TourItem) {

            binding.attractionName.text = model.name ?: "台北市景點"

            binding.attractionDescription.text = model.introduction ?: "台北市景點介紹"

            //image
            if (model.images?.isNotEmpty() == true && model.images.get(0) != null) {

                val src = model.images[0]?.src
                Picasso.get().load(src ?: "").placeholder(R.drawable.taipei_icon).into(binding.attractionImage)

            } else {

                Picasso.get().load(R.drawable.taipei_icon).into(binding.attractionImage)

            }

            binding.mainView.setOnClickListener {

                itemClick(model)

            }

        }

    }

    inner class FooterViewHolder(var binding: RvLoadingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            if (!showFooter) {

                binding.tvLoading.visibility = View.GONE

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (TYPE_NORMAL == viewType) {

            val itemView = RvMainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AttractionAdapterViewHolder(itemView)

        } else {

            val itemView = RvLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(itemView)

        }

    }

    override fun getItemCount(): Int {
        val count = currentList.size
        return if (showFooter && count > 0) count + 1 else count
    }

    override fun getItemViewType(position: Int): Int {

        if (currentList.isEmpty()) return TYPE_NORMAL

        return if (position == itemCount - 1 && showFooter) {

            TYPE_FOOTER

        } else {

            TYPE_NORMAL

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {

            TYPE_NORMAL -> {

                val item = currentList[position]
                item.let {

                    (holder as AttractionAdapterViewHolder).bind(it)

                }

            }

            else -> {//TYPE_FOOTER

                (holder as FooterViewHolder).bind()

            }


        }

    }

    //資料疊加
    fun updateData(data: MutableList<TourItem>?) {

        val newItems = mutableListOf<TourItem>()
        newItems.addAll(currentList)
        data?.let { newItems.addAll(it) }
        submitList(newItems)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {

        submitList(emptyList())

    }

    fun showFooter(show: Boolean) {

        if (currentList.isEmpty()) {

            showFooter = false

        } else if (showFooter != show) {

            showFooter = show
            submitList(currentList.toList())

        }

    }

    fun getAttractionsSize(): String = currentList.count().toString()

}

private class DiffItemCallback : DiffUtil.ItemCallback<TourItem>() {
    override fun areItemsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {

        return oldItem.id == newItem.id

    }

    override fun areContentsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {

        return oldItem == newItem

    }
}


