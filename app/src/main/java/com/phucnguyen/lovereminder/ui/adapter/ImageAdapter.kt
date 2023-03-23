package com.phucnguyen.lovereminder.ui.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.ItemPictureBinding
import com.phucnguyen.lovereminder.model.Image

class ImageAdapter(var context: Context) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    interface Listener {
        fun onItemClicked(position: Int)
        fun onItemLongClicked(position: Int)
    }

     var images: List<Image>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: Listener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPictureBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        images?.let {
            val data = it[position]
            holder.bind(data)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClicked(position)
        }
        holder.itemView.setOnLongClickListener {
            listener?.let {
                it.onItemLongClicked(position)
                true
            } ?: false
        }
    }

    override fun getItemCount(): Int {
        return images?.size ?: 0
    }

    class ViewHolder(private val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            Glide.with(binding.ivPicture)
                .load(image.uri)
                .centerCrop()
                .into(binding.ivPicture)

            if (image.isPendingDelete) {
                binding.ivPendingTrack.visibility = View.VISIBLE
                binding.ivPicture.alpha = 0.8f
            } else {
                binding.ivPendingTrack.visibility = View.GONE
                binding.ivPicture.alpha = 1f
            }
        }
    }
}