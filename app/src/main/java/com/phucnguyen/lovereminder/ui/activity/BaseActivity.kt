package com.phucnguyen.lovereminder.ui.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.viewmodel.BaseActivityViewModel
import kotlinx.coroutines.launch


open class BaseActivity : AppCompatActivity() {
    private val viewModel: BaseActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(viewModel.getCurrentTheme())

        setStatusBarColor()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themeFlow.collect {
                    if (viewModel.isThemeChanged()) {
                        viewModel.updatePreviousTheme()
                        //call recreate to apply the new theme
                        recreate()
                    }
                }
            }
        }
    }

    private fun setStatusBarColor() {
        val currentTheme = resources.newTheme()
        currentTheme.applyStyle(viewModel.getCurrentTheme(), true)
        val typedValue = TypedValue()
        currentTheme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        val colorPrimaryDark = typedValue.data

        val window: Window = window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = colorPrimaryDark
    }
}