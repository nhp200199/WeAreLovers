package com.phucnguyen.lovereminder.ui.fragment.dialog

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatDialogFragment
import android.os.Bundle
import android.util.Log
import com.phucnguyen.lovereminder.R
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.phucnguyen.lovereminder.databinding.ChangeDateBinding
import java.lang.ClassCastException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChangeDateDialog : AppCompatDialogFragment() {
    private var listener: Listener? = null
    private lateinit var binding: ChangeDateBinding

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
        val view = inflater.inflate(R.layout.change_date, null)
        binding = ChangeDateBinding.bind(view)
        builder.setView(view)
            .setTitle("Sửa ngày tháng bắt đầu")
            .setNegativeButton("Hủy") { dialogInterface, i -> }
            .setPositiveButton("Lưu") { dialogInterface, i ->
                val date = binding.edtDate.text.toString()
                var dateStart: Date? = null
                try {
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    dateStart = simpleDateFormat.parse(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Nhập sai định dạng", Toast.LENGTH_SHORT).show()
                }
                if (dateStart != null) listener!!.applyDateChange(date)
            }
        return builder.create()
    }

    interface Listener {
        fun applyDateChange(date: String)
    }

    companion object {
        private const val TAG = "MyCustomDialog"
    }
}