package com.example.lovereminder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovereminder.databinding.ActivityDiaryBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DiaryActivity extends AppCompatActivity{
    private TextView tv_Date;
    private TextView tv_Content;
    private EditText edt_Content;
    private Menu menu;

    private int diaryId;
    String originalText;
    private Diary currentDiary;
    private DiaryViewModel viewModel;
    private DiaryDao mDiaryDao;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDiaryBinding binding = ActivityDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mDiaryDao = AppDatabase.getInstance(this).getDiaryDao();
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        tv_Content = binding.tvContent;
        tv_Date = binding.tvDate;
        edt_Content = binding.edtContent;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (savedInstanceState != null){
            isEditing = savedInstanceState.getBoolean("isEditing");
            if (isEditing){
                edt_Content.setVisibility(View.VISIBLE);
                tv_Content.setVisibility(View.GONE);
            }
        }
        if (getIntent().hasExtra("id")){
            diaryId = getIntent().getIntExtra("id", 0);
        }

        viewModel.setDiaryId(diaryId);
        viewModel.getDiary().observe(this, diary -> {
            currentDiary = diary;
            originalText = currentDiary.getContent();
            tv_Content.setText(diary.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");
            String formattedDateString = sdf.format(new Date(diary.getDate()));
            tv_Date.setText(formattedDateString);
        });
    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Chưa lưu thay dổi. Bạn có muốn lưu lại không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDiary();
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DiaryActivity.super.onBackPressed();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit_diary, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEditing", isEditing);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditing){
            menu.findItem(R.id.action_edit_dairy).setVisible(false);
            menu.setGroupVisible(R.id.group_rewrite_diary_status, true);
        }
        else{
            menu.findItem(R.id.action_edit_dairy).setVisible(true);
            menu.setGroupVisible(R.id.group_rewrite_diary_status, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_dairy:
                isEditing = true;

                edt_Content.setVisibility(View.VISIBLE);
                edt_Content.setText(tv_Content.getText().toString());
                edt_Content.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_Content, InputMethodManager.SHOW_IMPLICIT);
                edt_Content.setSelection(edt_Content.getText().length());
                tv_Content.setVisibility(View.INVISIBLE);

                invalidateOptionsMenu();
                return true;
            case R.id.action_done_rewrite_diary:
                if (originalText.equals(edt_Content.getText().toString().trim())){
                    Toast.makeText(this, "Bạn chưa thay đổi nội dung nhật kí", Toast.LENGTH_SHORT).show();
                    return  true;
                }
                isEditing = false;
                tv_Content.setVisibility(View.VISIBLE);
                edt_Content.setVisibility(View.INVISIBLE);

                updateDiary();

                invalidateOptionsMenu();
                return true;
            case R.id.action_cancel_rewrite_diary:
                isEditing = false;
                tv_Content.setVisibility(View.VISIBLE);
                edt_Content.setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDiary() {
        currentDiary.setContent(edt_Content.getText().toString().trim());
//        new UpdateDiaryAsync(mDiaryDao).execute(currentDiary);
        mDiaryDao.updateDiary(currentDiary)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(DiaryActivity.this, "Diary updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!originalText.equals(edt_Content.getText().toString().trim()) && isEditing)
            showMessage();
        else super.onBackPressed();
    }

//    private static class UpdateDiaryAsync extends AsyncTask<Diary, Void, Integer>{
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
