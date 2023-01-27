package com.phucnguyen.lovereminder.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.phucnguyen.lovereminder.ui.adapter.ThemesAdapter.ThemeViewHolder
import com.phucnguyen.lovereminder.R
import android.content.SharedPreferences
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.phucnguyen.lovereminder.PREF_THEME_COLOR
import com.phucnguyen.lovereminder.SHARE_PREF_USER_PREFERENCE
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ThemesAdapter(private val mContext: Context) : RecyclerView.Adapter<ThemeViewHolder>() {
    private val colorIntsList = listOf(
        R.color.colorPrimary,
        R.color.blue
    )
    private val mSharedPreferences: SharedPreferences =
        mContext.getSharedPreferences(SHARE_PREF_USER_PREFERENCE, Context.MODE_PRIVATE)
    private var currentSelectedThemeIdPosition: Int
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.item_theme_color, parent, false)
        return ThemeViewHolder(v)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.civTheme.setImageResource(colorIntsList[position])
        if (position == currentSelectedThemeIdPosition) {
            holder.civThemeChecked.visibility = View.VISIBLE
        } else {
            holder.civThemeChecked.visibility = View.GONE
        }
        holder.civTheme.setOnClickListener { v: View? -> changeSelectedThemePosition(position) }
    }

    private fun changeSelectedThemePosition(newlySelectedThemeIdPosition: Int) {
        notifyItemChanged(currentSelectedThemeIdPosition) //unselect the old one
        currentSelectedThemeIdPosition = newlySelectedThemeIdPosition
        notifyItemChanged(currentSelectedThemeIdPosition)
    }

    override fun getItemCount(): Int {
        return colorIntsList.size
    }

    val currentSelectedThemeId: Int
        get() = colorIntsList[currentSelectedThemeIdPosition]

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civTheme: CircleImageView = itemView.findViewById(R.id.civThemeCorlor)
        val civThemeChecked: CircleImageView = itemView.findViewById(R.id.civThemeChecked)
    }

    init {
        val selectedThemeColorId = mSharedPreferences.getInt(PREF_THEME_COLOR, 0)
        currentSelectedThemeIdPosition = colorIntsList.indexOf(selectedThemeColorId)
    }
}