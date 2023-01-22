package com.phucnguyen.lovereminder.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.databinding.ActivityDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.viewmodel.DiaryViewModel
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class DiaryActivity : BaseActivity() {
    private var tv_Date: TextView? = null
    private var tv_Content: TextView? = null
    private var edt_Content: EditText? = null
    private var menu: Menu? = null
    private var diaryId = 0
    var originalText: String? = null
    private var currentDiary: Diary? = null
    private var viewModel: DiaryViewModel? = null
    private var mDiaryDao: DiaryDao? = null
    private var isEditing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        val binding = ActivityDiaryBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        mDiaryDao = getInstance(this).diaryDao
        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)
        tv_Content = binding.tvContent
        tv_Date = binding.tvDate
        edt_Content = binding.edtContent
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }
        if (savedInstanceState != null) {
            isEditing = savedInstanceState.getBoolean("isEditing")
            if (isEditing) {
                edt_Content!!.visibility = View.VISIBLE
                tv_Content!!.visibility = View.GONE
            }
        }
        if (intent.hasExtra("id")) {
            diaryId = intent.getIntExtra("id", 0)
        }

//        viewModel.setDiaryId(diaryId);
//        viewModel.getDiary().observe(this, diary -> {
//            currentDiary = diary;
//            originalText = currentDiary.getContent();
//            tv_Content.setText(diary.getContent());
//            SimpleDateFormat sdf = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");
//            String formattedDateString = sdf.format(new Date(diary.getDate()));
//            tv_Date.setText(formattedDateString);
//        });

//        viewModel.setCurrentDiaryId(diaryId);
//        viewModel.getCurrentDiaryObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(diary -> {
//                    currentDiary = diary;
//                    originalText = currentDiary.getContent();
//                    tv_Content.setText(diary.getContent());
//                    SimpleDateFormat sdf = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");
//                    String formattedDateString = sdf.format(new Date(diary.getDate()));
//                    tv_Date.setText(formattedDateString);
//                });
        mDiaryDao!!.getDiaryById(diaryId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { diary: Diary ->
                currentDiary = diary
                originalText = currentDiary!!.content
                tv_Content!!.text = diary.content
                val sdf = SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy")
                val formattedDateString = sdf.format(Date(diary.date))
                tv_Date!!.text = formattedDateString
            }
    }

    private fun showMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Chưa lưu thay dổi. Bạn có muốn lưu lại không?")
            .setPositiveButton("Có") { dialog, which ->
                updateDiary()
                finish()
            }
            .setNegativeButton("Không") { dialog, which -> super@DiaryActivity.onBackPressed() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the app bar.
        menuInflater.inflate(R.menu.menu_edit_diary, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isEditing", isEditing)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (isEditing) {
            menu.findItem(R.id.action_edit_dairy).isVisible = false
            menu.setGroupVisible(R.id.group_rewrite_diary_status, true)
        } else {
            menu.findItem(R.id.action_edit_dairy).isVisible = true
            menu.setGroupVisible(R.id.group_rewrite_diary_status, false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_dairy -> {
                isEditing = true
                edt_Content!!.visibility = View.VISIBLE
                edt_Content!!.setText(tv_Content!!.text.toString())
                edt_Content!!.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edt_Content, InputMethodManager.SHOW_IMPLICIT)
                edt_Content!!.setSelection(edt_Content!!.text.length)
                tv_Content!!.visibility = View.INVISIBLE
                invalidateOptionsMenu()
                true
            }
            R.id.action_done_rewrite_diary -> {
                if (originalText == edt_Content!!.text.toString().trim { it <= ' ' }) {
                    Toast.makeText(this, "Bạn chưa thay đổi nội dung nhật kí", Toast.LENGTH_SHORT)
                        .show()
                    return true
                }
                isEditing = false
                tv_Content!!.visibility = View.VISIBLE
                edt_Content!!.visibility = View.INVISIBLE
                updateDiary()
                invalidateOptionsMenu()
                true
            }
            R.id.action_cancel_rewrite_diary -> {
                isEditing = false
                tv_Content!!.visibility = View.VISIBLE
                edt_Content!!.visibility = View.INVISIBLE
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateDiary() {
        val updatedContent = edt_Content!!.text.toString().trim { it <= ' ' }
        currentDiary!!.content = updatedContent
        //        new UpdateDiaryAsync(mDiaryDao).execute(currentDiary);
        mDiaryDao!!.updateDiary(currentDiary!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    tv_Content!!.text = updatedContent
                    Toast.makeText(this@DiaryActivity, "Diary updated", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {}
            })
    }

    override fun onBackPressed() {
        if (originalText != edt_Content!!.text.toString()
                .trim { it <= ' ' } && isEditing
        ) showMessage() else super.onBackPressed()
    } //    private static class UpdateDiaryAsync extends AsyncTask<Diary, Void, Integer>{
    //        private DiaryDao mDiaryDao;
    //
    //        public UpdateDiaryAsync(DiaryDao diaryDao) {
    //            mDiaryDao = diaryDao;
    //        }
    //
    //        @Override
    //        protected Integer doInBackground(Diary... diaries) {
    //            return mDiaryDao.updateDiary(diaries[0]);
    //        }
    //
    //        @Override
    //        protected void onPostExecute(Integer updatedDiaryId) {
    //            super.onPostExecute(updatedDiaryId);
    //            Log.d("Tag", String.valueOf(updatedDiaryId));
    //        }
    //    }
}