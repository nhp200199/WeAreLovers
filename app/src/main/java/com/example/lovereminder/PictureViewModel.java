package com.example.lovereminder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PictureViewModel extends ViewModel {
    MutableLiveData<List<Image>> images = new MutableLiveData<List<Image>>();

    public LiveData<List<Image>> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images.setValue(images);
    }
}
