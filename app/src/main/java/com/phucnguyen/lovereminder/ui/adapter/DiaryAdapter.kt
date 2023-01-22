package com.phucnguyen.lovereminder.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.ui.adapter.DiaryAdapter.DiaryViewHolder
import java.text.SimpleDateFormat
import java.util.*

class DiaryAdapter(private val mContext: Context) : ListAdapter<Diary, DiaryViewHolder>(
    DIFF_CALLBACK
) {
    interface Listener {
        fun onDiaryLongClickListener(diary: Diary?, v: View?)
        fun onDiaryClickListener(diary: Diary?)
    }

    fun setListener(listener: Listener?) {
        mListener = listener
    }

    private var mListener: Listener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val convertView = LayoutInflater.from(mContext).inflate(
            R.layout.diary_item, parent,
            false
        )
        return DiaryViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = getItem(position)
        //transform the date retrieved from database
        val dateToBeFormat = diary!!.date
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val dateObject = Date(dateToBeFormat)
        val formattedDateString = simpleDateFormat.format(dateObject)
        holder.tv_date.text = formattedDateString
        holder.tv_content.text = diary.content
        holder.itemView.setOnClickListener {
            if (mListener != null) mListener!!.onDiaryClickListener(
                diary
            )
        }
        holder.itemView.setOnLongClickListener { v ->
            if (mListener != null) mListener!!.onDiaryLongClickListener(diary, v)
            true
        }
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_date: TextView = itemView.findViewById<View>(R.id.tv_date) as TextView
        var tv_content: TextView = itemView.findViewById<View>(R.id.tv_content) as TextView
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Diary> =
            object : DiffUtil.ItemCallback<Diary>() {
                override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                    return oldItem.content == newItem.content
                }
            }
    }
}