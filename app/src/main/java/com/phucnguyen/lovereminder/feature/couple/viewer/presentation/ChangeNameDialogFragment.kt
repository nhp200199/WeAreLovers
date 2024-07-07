package com.phucnguyen.lovereminder.feature.couple.viewer.presentation

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.phucnguyen.lovereminder.core.base.presentation.BaseDialogFragment
import com.phucnguyen.lovereminder.databinding.LayoutDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNameDialogFragment : BaseDialogFragment<LayoutDialogBinding>() {
    private val viewModel: ChangeNameDialogViewModel by viewModels()
    override fun getClassTag(): String {
        return ChangeNameDialogFragment::class.java.simpleName
    }

    override fun getViewBindingClass(
        inflater: LayoutInflater,
    ): LayoutDialogBinding {
        return LayoutDialogBinding.inflate(inflater)
    }

    override fun setupView() {
        if (requireArguments().containsKey("name")) {
            val name = requireArguments().getString("name")
            binding.edtUserName.setText(name)
        }
    }

    override fun setViewListener() {

    }

    override fun createDialog(context: Context): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
            .setTitle("Sửa Tên")
            .setNegativeButton("Hủy") { dialogInterface, i -> }
            .setPositiveButton("Lưu") { dialogInterface, i ->
                viewModel.onNameChanged(getChangedTarget(), binding.edtUserName.text.trim().toString())
            }

        return builder.create()
    }

    private fun getChangedTarget(): String? {
        return if (requireArguments().containsKey("target")) {
            requireArguments().getString("target")
        } else {
            null
        }
    }
}