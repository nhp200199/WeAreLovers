package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import io.reactivex.Observable
import io.reactivex.subjects.Subject

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val mDiaryDao: DiaryDao
    private val diaryId = MutableLiveData<Int>()
    val diary: LiveData<Diary>? = null
    private val diaryIdSubject: Subject<Int>? = null
    private val currentDiaryObservable: Observable<Diary>? = null

    init {
        mDiaryDao = getInstance(application).diaryDao
        //diary = Transformations.switchMap(diaryId, id -> mDiaryDao.getDiaryById(id));
//        diaryIdSubject = BehaviorSubject.create();
//        currentDiaryObservable = diaryIdSubject.switchMap(id -> mDiaryDao.getDiaryById(id));
    }

    fun setDiaryId(diaryId: Int) {
        this.diaryId.value = diaryId
    } //    public void setCurrentDiaryId(int diaryId) {
    //        diaryIdSubject.onNext(diaryId);
    //    }
    //
    //    public Observable<Diary> getCurrentDiaryObservable() {
    //        return currentDiaryObservable;
    //    }
}