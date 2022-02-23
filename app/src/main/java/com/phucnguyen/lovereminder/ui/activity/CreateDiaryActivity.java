package com.phucnguyen.lovereminder.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.phucnguyen.lovereminder.database.AppDatabase;
import com.phucnguyen.lovereminder.model.Diary;
import com.phucnguyen.lovereminder.database.DiaryDao;
import com.phucnguyen.lovereminder.R;
import com.phucnguyen.lovereminder.databinding.ActivityCreateDiaryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateDiaryActivity extends BaseActivity implements View.OnClickListener{
    private Button btnSave;
    private EditText edt_diary;
    private ImageView img_background;

    private boolean isSaved = false;
    private SharedPreferences sharedPreferences1;

    private DiaryDao mDiaryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        ActivityCreateDiaryBinding binding = ActivityCreateDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        btnSave = binding.btnSave;
        edt_diary = binding.edtDiary;
        img_background = binding.imgBackground;

        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE);

        if(sharedPreferences1.contains("picture"))
        {
            Glide.with(this)
                    .load(Uri.parse(sharedPreferences1.getString("picture", null)))
                    .into(img_background);
        }

        btnSave.setEnabled(false);
        btnSave.setOnClickListener(this);

        edt_diary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = edt_diary.getText().toString().trim();
                btnSave.setEnabled(!string.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDiaryDao = AppDatabase.getInstance(this).getDiaryDao();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                saveDiary();
                break;
        }
    }

    private void saveDiary() {
        Calendar calendar = Calendar.getInstance();
        String content = edt_diary.getText().toString();

        Diary diary = new Diary();
        diary.setDate(calendar.getTimeInMillis());
        diary.setContent(content);

//        new InsertDiaryAsync(this, mDiaryDao).execute(diary);
        mDiaryDao.insertDiary(diary)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(CreateDiaryActivity.this, "Đã lưu", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
        edt_diary.setText("");
        hideKeyboard(this);
    }

    @Override
    public void onBackPressed() {
        if(edt_diary.getText().toString().equals(""))
            CreateDiaryActivity.super.onBackPressed();
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Chưa lưu đoạn nhật kí kìa ấy ơi. Bạn có muốn lưu lại không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveDiary();
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
//    private static class InsertDiaryAsync extends AsyncTask<Diary, Void, Void>{
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
