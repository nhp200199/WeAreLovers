package com.example.lovereminder;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.lovereminder.PictureFragment.PICTURES_FOLDER_NAME;

public class PictureViewModel extends AndroidViewModel {
    MutableLiveData<List<Image>> images = new MutableLiveData<List<Image>>();

    public PictureViewModel(@NonNull @NotNull Application application) {
        super(application);
        List<Image> imagesList = new ArrayList<Image>();
        File file = new File(application.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), PICTURES_FOLDER_NAME);
        File[] imagesFiles = file.listFiles();
        for (File imagesFile : imagesFiles) {
            imagesFile.listFiles();
            Uri imageUri = Uri.fromFile(imagesFile);
            Image image = new Image(imageUri, "");
            imagesList.add(image);
        }
        images = new MutableLiveData<List<Image>>(imagesList);
    }

    public LiveData<List<Image>> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images.setValue(images);
    }
}
