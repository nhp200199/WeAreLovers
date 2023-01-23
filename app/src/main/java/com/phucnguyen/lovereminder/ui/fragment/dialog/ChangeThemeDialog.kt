package com.phucnguyen.lovereminder.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.ui.adapter.ThemesAdapter

class ChangeThemeDialog : AppCompatDialogFragment() {
    interface ThemeDialogListener {
        fun onThemeDialogChanged(themeId: Int)
    }

    private var rcvThemes: RecyclerView? = null
    private var adapter: ThemesAdapter? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            activity!!
        )
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_change_theme, null)
        connectView(view)
        builder.setView(view)
            .setTitle("Thay đổi theme")
            .setNegativeButton("Hủy") { dialogInterface, i -> }
            .setPositiveButton("Xác nhận") { dialogInterface, i ->
                dismiss()
                val themeId = adapter!!.currentSelectedThemeId
                (targetFragment as ThemeDialogListener?)!!.onThemeDialogChanged(themeId)
            }
        return builder.create()
    }

    private fun connectView(view: View) {
        rcvThemes = view.findViewById(R.id.rcvThemes)
        rcvThemes?.setLayoutManager(
            LinearLayoutManager(
                view.context,
                RecyclerView.HORIZONTAL,
                false
            )
        )
        adapter = ThemesAdapter(view.context)
        rcvThemes?.setAdapter(adapter)
    }
}