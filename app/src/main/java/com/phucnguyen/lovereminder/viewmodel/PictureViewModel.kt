package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import java.io.File

class PictureViewModel(application: Application) : AndroidViewModel(application) {
    private var _images = MutableLiveData<List<Image>>()
    var images: LiveData<List<Image>> = _images
    init {
        val imagesList: MutableList<Image> = ArrayList()
        val file = File(application.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), PictureFragment.PICTURES_FOLDER_NAME)
        val imagesFiles = file.listFiles()
        if (imagesFiles != null) {
            for (imagesFile in imagesFiles) {
                imagesFile.listFiles()
                val imageUri = Uri.fromFile(imagesFile)
                val image = Image(imageUri, "")
                imagesList.add(image)
            }
        }
        _images = MutableLiveData(imagesList)
    }

    fun setImages(images: List<Image>) {
        this._images.value = images
    }
}