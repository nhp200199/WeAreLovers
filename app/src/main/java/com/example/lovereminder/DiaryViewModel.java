package com.example.lovereminder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;

public class DiaryViewModel extends AndroidViewModel {
    private DiaryDao mDiaryDao;
    private MutableLiveData<Integer> diaryId = new MutableLiveData<>();
    private LiveData<Diary> diary;

    public DiaryViewModel(@NonNull @NotNull Application application) {
        super(application);
        mDiaryDao = AppDatabase.getInstance(application).getDiaryDao();
        diary = Transformations.switchMap(diaryId, id -> mDiaryDao.getDiaryById(id));
    }

    public LiveData<Diary> getDiary() {
        return diary;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId.setValue(diaryId);
    }
}
