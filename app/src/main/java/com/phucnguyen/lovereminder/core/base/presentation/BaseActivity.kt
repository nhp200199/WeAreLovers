package com.phucnguyen.lovereminder.core.base.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T: ViewBinding>: AppCompatActivity() {
    protected lateinit var binding: T

    protected abstract fun getClassTag(): String

    protected abstract fun getViewBindingClass(inflater: LayoutInflater): T

    protected abstract fun setupView()

    protected abstract fun setViewListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.i(getClassTag(), "onCreate()")
        binding = getViewBindingClass(layoutInflater)
        setContentView(binding.root)

        setupView()
        setViewListener()
    }

    override fun onStart() {
        super.onStart()
        Log.i(getClassTag(), "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.i(getClassTag(), "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.i(getClassTag(), "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.i(getClassTag(), "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "onDestroy()")
    }
}