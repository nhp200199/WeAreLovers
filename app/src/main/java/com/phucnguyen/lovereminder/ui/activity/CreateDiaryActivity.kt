package com.phucnguyen.lovereminder.ui.activity

import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.databinding.ActivityCreateDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class CreateDiaryActivity : BaseActivity(), View.OnClickListener {
    private var btnSave: Button? = null
    private var edt_diary: EditText? = null
    private var img_background: ImageView? = null
    private val isSaved = false
    private lateinit var sharedPreferences1: SharedPreferences
    private var mDiaryDao: DiaryDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        val binding = ActivityCreateDiaryBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }
        btnSave = binding.btnSave
        edt_diary = binding.edtDiary
        img_background = binding.imgBackground
        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE)
        if (sharedPreferences1.contains("picture")) {
            Glide.with(this)
                .load(Uri.parse(sharedPreferences1.getString("picture", null)))
                .into(img_background!!)
        }
        btnSave!!.isEnabled = false
        btnSave!!.setOnClickListener(this)
        edt_diary!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = edt_diary!!.text.toString().trim { it <= ' ' }
                btnSave!!.isEnabled = !string.isEmpty()
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
        val content = edt_diary!!.text.toString()
        val diary = Diary(0, calendar.timeInMillis, content)

//        new InsertDiaryAsync(this, mDiaryDao).execute(diary);
        mDiaryDao!!.insertDiary(diary)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Toast.makeText(this@CreateDiaryActivity, "Đã lưu", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {}
            })
        edt_diary!!.setText("")
        hideKeyboard(this)
    }

    override fun onBackPressed() {
        if (edt_diary!!.text.toString() == "") super@CreateDiaryActivity.onBackPressed() else {
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

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } //    private static class InsertDiaryAsync extends AsyncTask<Diary, Void, Void>{
        //        private DiaryDao mDiaryDao;
        //        private Context context;
        //
        //        public InsertDiaryAsync(Context context, DiaryDao diaryDao) {
        //            mDiaryDao = diaryDao;
        //            this.context = context;
        //        }
        //
        //        @Override
        //        protected Void doInBackground(Diary... diaries) {
        //            return mDiaryDao.insertDiary(diaries[0]);
        //        }
        //
        //        @Override
        //        protected void onPostExecute(Void unused) {
        //            super.onPostExecute(unused);
        //            Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
        //        }
        //    }
    }
}