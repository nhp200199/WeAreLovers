package com.phucnguyen.lovereminder.core.base.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<T: ViewBinding> : DialogFragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract fun getClassTag(): String

    protected abstract fun getViewBindingClass(inflater: LayoutInflater): T

    protected abstract fun setupView()

    protected abstract fun setViewListener()

    protected abstract fun createDialog(context: Context): Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(getClassTag(), "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        _binding = getViewBindingClass(inflater)

        setupView()
        setViewListener()

        return createDialog(requireContext())
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