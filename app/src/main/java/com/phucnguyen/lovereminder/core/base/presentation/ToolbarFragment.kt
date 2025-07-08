package com.phucnguyen.lovereminder.core.base.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import com.phucnguyen.lovereminder.R

abstract class ToolbarFragment<T: ViewBinding> : BaseFragment<T>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowInsets(view)
    }

    private fun applyWindowInsets(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        if (toolbar != null) {
            val initialToolbarHeight = toolbar.layoutParams.height
            ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                val topInset = insets.top

                v.updateLayoutParams {
                    height = topInset + initialToolbarHeight
                }
                v.setPadding(v.paddingLeft, topInset, v.paddingRight, v.paddingBottom)
                windowInsets
            }
        }
    }
}