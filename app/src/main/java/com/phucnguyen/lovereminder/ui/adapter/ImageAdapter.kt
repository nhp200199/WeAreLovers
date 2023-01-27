package com.phucnguyen.lovereminder.ui.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.model.Image

class ImageAdapter(var context: Context, imagesInput: List<Image>) : BaseAdapter() {
     var images = imagesInput
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val imagesToDelete: SparseBooleanArray = SparseBooleanArray()

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val imageView = ImageView(context)
        if (imagesToDelete[position]) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = ViewGroup.LayoutParams(330, 350)
            imageView.cropToPadding = true
            Glide.with(context)
                .load(images[position].uri)
                .thumbnail(0.5f)
                .into(imageView)
            imageView.setBackgroundResource(R.drawable.view_border)
        } else {
            Glide.with(context)
                .load(images[position].uri)
                .into(imageView)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = ViewGroup.LayoutParams(330, 350)
        }
        return imageView
    }

    override fun isEnabled(position: Int): Boolean {
        return super.isEnabled(position)
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

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
}