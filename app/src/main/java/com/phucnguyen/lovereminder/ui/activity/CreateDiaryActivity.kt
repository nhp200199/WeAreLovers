package com.phucnguyen.lovereminder.ui.activity

import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.PREF_PICTURE
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.SHARE_PREF_BACKGROUND
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.databinding.ActivityCreateDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.utils.hideKeyboard
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class CreateDiaryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mDiaryDao: DiaryDao
    private lateinit var binding: ActivityCreateDiaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
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

        sharedPreferences = getSharedPreferences(SHARE_PREF_BACKGROUND, MODE_PRIVATE)
        if (sharedPreferences.contains(PREF_PICTURE)) {
            Glide.with(this)
                .load(Uri.parse(sharedPreferences.getString(PREF_PICTURE, null)))
                .into(binding.imgBackground)
        }
        binding.btnSave.isEnabled = false
        binding.btnSave.setOnClickListener(this)
        binding.edtDiary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = binding.edtDiary.text.toString().trim { it <= ' ' }
                binding.btnSave.isEnabled = string.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mDiaryDao = getInstance(this).diaryDao
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

        mDiaryDao.insertDiary(diary)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Toast.makeText(this@CreateDiaryActivity, "Đã lưu", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {}
            })
        binding.edtDiary.setText("")
        hideKeyboard(this)
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