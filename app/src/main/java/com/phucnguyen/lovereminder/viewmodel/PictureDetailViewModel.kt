package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.repository.PictureRepo
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PictureDetailViewModel @Inject constructor(private val pictureRepo: PictureRepo) : ViewModel() {
    var currentImagePos: Int = 0
    val images: LiveData<List<Image>> = pictureRepo.getImagesStream()

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch() {
            pictureRepo.loadPictures(false)
        }
    }

    fun deleteCurrentImage() {
        viewModelScope.launch {
            deleteImage(images.value!![currentImagePos].id)
        }
    }

    private suspend fun deleteImage(id: Long): Int = pictureRepo.deleteImage(id)

    companion object {
        private val TAG = PictureDetailViewModel::class.java.simpleName
    }
}