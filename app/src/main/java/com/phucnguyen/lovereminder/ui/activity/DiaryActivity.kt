package com.phucnguyen.lovereminder.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.ActivityDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.viewmodel.DiaryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DiaryActivity : BaseActivity() {
    private var menu: Menu? = null
    private var diaryId = 0
    private lateinit var viewModel: DiaryViewModel
    private lateinit var binding: ActivityDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }
        if (intent.hasExtra(EXTRA_DIARY_ID)) {
            diaryId = intent.getIntExtra(EXTRA_DIARY_ID, DEFAULT_DIARY_ID)
        }

        lifecycleScope.launch { 
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isEditingFlow.collect {
                        if (it) {
                            binding.edtContent.visibility = View.VISIBLE
                            binding.tvContent.visibility = View.GONE
                            binding.edtContent.setText(binding.tvContent.text.toString())
                            binding.edtContent.requestFocus()
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(binding.edtContent, InputMethodManager.SHOW_IMPLICIT)
                            binding.edtContent.setSelection(binding.edtContent.text.length)
                        } else {
                            binding.edtContent.visibility = View.GONE
                            binding.tvContent.visibility = View.VISIBLE
                        }
                    }
                }
                launch {
                    viewModel.findDiaryById(diaryId).collect {
                        populateDiaryView(it)
                    }
                }
            }
        }
    }

    private fun populateDiaryView(diary: Diary) {
        binding.tvContent.text = diary.content
        val sdf = SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy")
        val formattedDateString = sdf.format(Date(diary.date))
        binding.tvDate.text = formattedDateString
    }

    private fun showMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.title_dialog_change_unsave_confirmation))
            .setPositiveButton(getString(R.string.txt_dialog_action_positive)) { dialog, which ->
                lifecycleScope.launch {
                    updateDiary()
                    dialog.dismiss()
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.txt_dialog_action_negative)) { dialog, which ->
                dialog.dismiss()
                super@DiaryActivity.onBackPressed()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the app bar.
        menuInflater.inflate(R.menu.menu_edit_diary, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isEditingDiary()) {
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
                viewModel.setDiaryEditingState(true)
                invalidateOptionsMenu()
                true
            }
            R.id.action_done_rewrite_diary -> {
                if (!viewModel.isDiaryContentChanged(binding.edtContent.text.toString().trim { it <= ' ' })) {
                    Toast.makeText(this, getString(R.string.msg_toast_diary_content_unchanged), Toast.LENGTH_SHORT)
                        .show()
                    return true
                }
                viewModel.setDiaryEditingState(false)
                lifecycleScope.launch {
                    updateDiary()
                }
                invalidateOptionsMenu()
                true
            }
            R.id.action_cancel_rewrite_diary -> {
                viewModel.setDiaryEditingState(false)
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun updateDiary() {
        val newContent = binding.edtContent.text.toString().trim { it <= ' ' }
        val updateResult = viewModel.updateDiary(newContent)
        if (updateResult) {
            Toast.makeText(this@DiaryActivity, getString(R.string.toast_msg_update_diary_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@DiaryActivity, getString(R.string.toast_msg_update_diary_failed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (viewModel.isDiaryContentChanged(binding.edtContent.text.toString().trim { it <= ' ' } )
                && viewModel.isEditingDiary()
        ) {
            showMessage()
        }
        else {
            super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_DIARY_ID = "id"
        const val DEFAULT_DIARY_ID = 0
    }
}