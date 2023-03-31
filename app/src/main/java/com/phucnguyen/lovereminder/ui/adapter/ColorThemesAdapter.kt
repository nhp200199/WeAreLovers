package com.phucnguyen.lovereminder.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.model.ColorTheme
import com.phucnguyen.lovereminder.ui.adapter.ColorThemesAdapter.ThemeViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class ColorThemesAdapter(
) : RecyclerView.Adapter<ThemeViewHolder>() {
    private var colorThemes: List<ColorTheme> = listOf()

    interface Listener {
        fun onColorThemeSelected(themeColorId: Int)
    }

    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_theme_color, parent, false)
        return ThemeViewHolder(v)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.civColorTheme.setImageResource(colorThemes[position].colorThemeResId)
        holder.makerView.background = ColorDrawable(holder.itemView.context.getColor(colorThemes[position].colorThemeResId))
        if (colorThemes[position].isSelected) {
            holder.makerView.visibility = View.VISIBLE
        } else {
            holder.makerView.visibility = View.GONE
        }
        holder.civColorTheme.setOnClickListener { v: View? -> listener?.onColorThemeSelected(colorThemes[position].colorThemeResId) }
    }

    override fun getItemCount(): Int {
        return colorThemes.size
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setThemesList(colorThemes: List<ColorTheme>) {
        this.colorThemes = colorThemes
        notifyDataSetChanged()
    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civColorTheme: CircleImageView = itemView.findViewById(R.id.civColorTheme)
        val makerView: View = itemView.findViewById(R.id.marker_view)
    }
}