package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(private val pictureRepo: PictureRepo) : ViewModel() {
    val pictures = pictureRepo.getImagesStream()
    private var _pendingDeleteImage = Transformations.map(pictures) {
        it.filter { image -> image.isPendingDelete }
    }
    val isChoosingImageToDeleteStream = Transformations.map(_pendingDeleteImage) {
        it.isNotEmpty()
    }

    fun isChoosingImageToDelete() = isChoosingImageToDeleteStream.value!!

    fun numberOfPendingImages() = _pendingDeleteImage.value?.size ?: 0

    fun loadImages() {
        viewModelScope.launch() {
            pictureRepo.loadPictures(true)
        }
    }

    suspend fun saveImages(bitmaps: List<Bitmap>) {
        pictureRepo.savePictures(bitmaps)
    }

    fun deletePendingImages() {
        val ids = _pendingDeleteImage.value!!
            .map { it.id }
        viewModelScope.launch { pictureRepo.deleteImages(ids) }
    }

    fun togglePictureDeleteStatus(position: Int) {
        val currentImages = pictures.value!!
        currentImages[position].toggleCheck()
        pictureRepo.updateList(currentImages)
    }

    fun clearAllPendingDeletePicture() {
        val currentImages = pictures.value!!
        currentImages.forEach {
            it.isPendingDelete = false
        }
        pictureRepo.updateList(currentImages)
    }

    companion object {
        private val TAG = PictureViewModel::class.java.simpleName
    }
}