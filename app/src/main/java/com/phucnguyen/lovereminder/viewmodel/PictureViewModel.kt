package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.tabs.TabLayout.TabGravity
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PictureViewModel(application: Application) : AndroidViewModel(application) {
    private var _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>> = _images
//    init {
//        val imagesList: MutableList<Image> = ArrayList()
//        val file = File(application.getExternalFilesDir(
//                Environment.DIRECTORY_PICTURES), PictureFragment.PICTURES_FOLDER_NAME)
//        val imagesFiles = file.listFiles()
//        if (imagesFiles != null) {
//            for (imagesFile in imagesFiles) {
//                imagesFile.listFiles()
//                val imageUri = Uri.fromFile(imagesFile)
//                val image = Image(imageUri, "")
//                imagesList.add(image)
//            }
//        }
//        _images = MutableLiveData(imagesList)
//    }

    fun setImages(images: List<Image>) {
        this._images.value = images
    }

    fun loadImages() {
        viewModelScope.launch {
            val images = queryImages()
            _images.postValue(images)
        }
    }

    private suspend fun queryImages(): List<Image> {
        val images = mutableListOf<Image>()

        withContext(Dispatchers.IO) {

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            )

            val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"

            val selectionArgs = arrayOf("${PictureFragment.PICTURE_PREFIX}%")

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            getApplication<Application>().contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val idColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)


                Log.i(TAG, "Found ${cursor.count} images")
                while (cursor.moveToNext()) {

                    // Here we'll use the column indexes that we found above.
                    val displayName = cursor.getString(displayNameColumn)
                    val id = cursor.getLong(idColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val image = Image(contentUri, displayName)
                    images += image

                    // For debugging, we'll output the image objects we create to logcat.
                    Log.v(TAG, "Added image: $image")
                }
            }
        }

        Log.v(TAG, "Found ${images.size} images")
        return images
    }

//    fun insert

    companion object {
        private val TAG = PictureViewModel::class.java.simpleName
    }
}