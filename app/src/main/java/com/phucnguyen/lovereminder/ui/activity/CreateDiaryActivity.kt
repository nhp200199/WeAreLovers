package com.phucnguyen.lovereminder.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.ActivityCreateDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.utils.hideKeyboard
import com.phucnguyen.lovereminder.viewmodel.CreateDiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CreateDiaryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCreateDiaryBinding
    private val viewModel: CreateDiaryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDiaryBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }

        viewModel.getBackgroundImage()?.apply {
            Glide.with(this@CreateDiaryActivity)
                .load(this@apply)
                .into(binding.imgBackground)
        }
        binding.btnSave.setOnClickListener(this)
        binding.edtDiary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = binding.edtDiary.text.toString().trim { it <= ' ' }
                binding.btnSave.isEnabled = string.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_save -> saveDiary()
        }
    }

    private fun saveDiary() {
        val calendar = Calendar.getInstance()
        val content = binding.edtDiary.text.toString()
        val diary = Diary(0, calendar.timeInMillis, content)

        lifecycleScope.launch {
            val diaryId = viewModel.createDiary(diary)
            if (diaryId > 0 ) {
                Toast.makeText(this@CreateDiaryActivity, getString(R.string.toast_msg_diary_created), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CreateDiaryActivity, getString(R.string.toast_msg_fail_create_diary), Toast.LENGTH_SHORT).show()
            }
            binding.edtDiary.setText("")
            hideKeyboard(this@CreateDiaryActivity)
        }
    }

    override fun onBackPressed() {
        if (binding.edtDiary.text.toString() == "") {
            super@CreateDiaryActivity.onBackPressed()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Chưa lưu đoạn nhật kí kìa ấy ơi. Bạn có muốn lưu lại không?")
                .setPositiveButton("Có") { dialog, which -> saveDiary() }
                .setNegativeButton("Không") { dialog, which ->
                    dialog.cancel()
                    finish()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}