package com.phucnguyen.lovereminder.ui.fragment.dialog

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatDialogFragment
import android.widget.EditText
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.phucnguyen.lovereminder.R
import java.lang.ClassCastException

class DialogFragment : AppCompatDialogFragment() {
    private var editTextUsername: EditText? = null
    private var listener: Listener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as Listener?
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException : " + e.message)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            activity!!
        )
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.layout_dialog, null)
        builder.setView(view)
            .setTitle("Sửa Tên")
            .setNegativeButton("Hủy") { dialogInterface, i -> }
            .setPositiveButton("Lưu") { dialogInterface, i ->
                val username = editTextUsername!!.text.toString()
                listener!!.ApplyChange(username)
            }
        val name = arguments!!.getString("name")
        editTextUsername = view.findViewById(R.id.edt_userName)
        editTextUsername?.setText(name)
        return builder.create()
    }

    interface Listener {
        fun ApplyChange(username: String?)
    }

    companion object {
        private const val TAG = "MyCustomDialog"
    }
}