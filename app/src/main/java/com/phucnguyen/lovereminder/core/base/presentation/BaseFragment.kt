package com.phucnguyen.lovereminder.core.base.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T: ViewBinding> : Fragment(){
    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract fun getClassTag(): String

    protected abstract fun getViewBindingClass(inflater: LayoutInflater, container: ViewGroup?): T

    protected abstract fun setupView()

    protected abstract fun setViewListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(getClassTag(), "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(getClassTag(), "onCreateView()")
        _binding = getViewBindingClass(inflater, container)

        setupView()
        setViewListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(getClassTag(), "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        Log.i(getClassTag(), "onAttach()")
        super.onAttach(context)
    }

    override fun onStart() {
        Log.i(getClassTag(), "onStart()")
        super.onStart()
    }

    override fun onResume() {
        Log.i(getClassTag(), "onResume()")
        super.onResume()
    }

    override fun onDetach() {
        Log.i(getClassTag(), "onDetach()")
        super.onDetach()
    }

    override fun onStop() {
        Log.i(getClassTag(), "onStop()")
        super.onStop()
    }

    override fun onPause() {
        Log.i(getClassTag(), "onPause()")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.i(getClassTag(), "onDestroyView()")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.i(getClassTag(), "onDestroy()")
        super.onDestroy()
    }
}