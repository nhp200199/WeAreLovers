package com.phucnguyen.lovereminder.ui.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
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
    val imagesToDelete: SparseBooleanArray = SparseBooleanArray()
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

//    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
//        val imageView = ImageView(context)
//        if (imagesToDelete[position]) {
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.layoutParams = ViewGroup.LayoutParams(330, 350)
//            imageView.cropToPadding = true
//            Glide.with(context)
//                .load(images[position].uri)
//                .thumbnail(0.5f)
//                .into(imageView)
//            imageView.setBackgroundResource(R.drawable.view_border)
//        } else {
//            Glide.with(context)
//                .load(images[position].uri)
//                .into(imageView)
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.layoutParams = ViewGroup.LayoutParams(330, 350)
//        }
//        return imageView
//    }

    fun toggleImagePositionToDelete(position: Int): Boolean {
        val result: Boolean
        if (imagesToDelete[position]) {
            result = false
            imagesToDelete.delete(position)
        } else {
            result = true
            imagesToDelete.put(position, true)
        }
        notifyDataSetChanged()
        return result
    }

    fun resetTracker() {
        imagesToDelete.clear()
    }

    class ViewHolder(private val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            Glide.with(binding.root.context)
                .load(image.uri)
                .into(binding.ivPicture)

            if (image.isPendingDelete) {
                binding.ivPicture.setBackgroundResource(R.drawable.view_border)
            } else {
                binding.ivPicture.setBackgroundResource(android.R.color.transparent)
            }
        }
    }
}