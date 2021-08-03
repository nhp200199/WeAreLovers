package com.example.lovereminder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class DiaryViewModel extends AndroidViewModel {
    private DiaryDao mDiaryDao;
    private MutableLiveData<Integer> diaryId = new MutableLiveData<>();
    private LiveData<Diary> diary;
    private Subject<Integer> diaryIdSubject;
    private Observable<Diary> currentDiaryObservable;

    public DiaryViewModel(@NonNull @NotNull Application application) {
        super(application);
        mDiaryDao = AppDatabase.getInstance(application).getDiaryDao();
        //diary = Transformations.switchMap(diaryId, id -> mDiaryDao.getDiaryById(id));
//        diaryIdSubject = BehaviorSubject.create();
//        currentDiaryObservable = diaryIdSubject.switchMap(id -> mDiaryDao.getDiaryById(id));
    }

    public LiveData<Diary> getDiary() {
        return diary;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId.setValue(diaryId);
    }

//    public void setCurrentDiaryId(int diaryId) {
//        diaryIdSubject.onNext(diaryId);
//    }
//
//    public Observable<Diary> getCurrentDiaryObservable() {
//        return currentDiaryObservable;
//    }
}
