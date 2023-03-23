package com.phucnguyen.lovereminder.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.phucnguyen.lovereminder.model.Image

interface PictureRepo {
    fun getImagesStream(): LiveData<List<Image>>
    suspend fun loadPictures(observeChanges: Boolean)
    suspend fun savePictures(bitmaps: List<Bitmap>)
    suspend fun deleteImages(ids: List<Long>)
    suspend fun deleteImage(id: Long): Int
    fun updateList(newList: List<Image>)
}