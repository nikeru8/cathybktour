package com.daniel.cathybktour.view.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.RvLoadingBinding
import com.daniel.cathybktour.databinding.RvMainItemBinding
import com.squareup.picasso.Picasso

class TourAdapter(private val itemClick: (TourItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var TYPE_NORMAL = 0
    private var TYPE_FOOTER = 1

    var totalAttractions = mutableListOf<TourItem>()

    inner class AttractionAdapterViewHolder(var binding: RvMainItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("CheckResult", "SetTextI18n")
        fun bind(model: TourItem) {

            binding.attractionName.text = model.name ?: "台北市景點"

            binding.attractionDescription.text = model.introduction ?: "台北市景點介紹"

            //image
            if (model.images?.isNotEmpty() == true && model.images?.get(0) != null) {

                val src = model.images?.get(0)?.src
                Picasso.get().load(src ?: "").placeholder(R.drawable.taipei_icon).into(binding.attractionImage)

            } else {

                Picasso.get().load(R.drawable.taipei_icon).into(binding.attractionImage)

            }

            binding.mainView.setOnClickListener {

                itemClick(model)

            }

        }

    }

    inner class FooterViewHolder(binding: RvLoadingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {


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

        return if (totalAttractions.size != 0) (totalAttractions.count().plus(1)) ?: 0 else 0

    }

    override fun getItemViewType(position: Int): Int {

        return if (position == itemCount - 1) {

            TYPE_FOOTER

        } else

            TYPE_NORMAL

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {

            TYPE_NORMAL -> {

                val item = totalAttractions[position]
                item.let {

                    (holder as AttractionAdapterViewHolder).bind(it)

                }

            }

            else -> {//TYPE_FOOTER

                (holder as FooterViewHolder).bind()

            }


        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUser(data: MutableList<TourItem>) {

        totalAttractions.addAll(data)
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {

        totalAttractions.clear()
        notifyDataSetChanged()

    }

    fun getAttractionsSize(): String = totalAttractions.count().toString()

}


