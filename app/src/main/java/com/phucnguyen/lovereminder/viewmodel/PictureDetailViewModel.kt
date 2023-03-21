package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PictureDetailViewModel(application: Application) : AndroidViewModel(application) {
    private var _images = MutableLiveData<List<Image>>()
    private var contentObserver: ContentObserver? = null
    var currentImagePos: Int = 0
    val images: LiveData<List<Image>> = _images

    init {
        loadImages()
    }
    fun loadImages() {
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                val images = queryImages()
                _images.postValue(images)
            }

            //register listener to update data whenever media store for uri changes
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        loadImages()
                    }
                }
                val uri = getExternalUri()
                getApplication<Application>().contentResolver.registerContentObserver(uri, true, contentObserver!!)
            }
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

            val collection = getExternalUri()

            getApplication<Application>().contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
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

                    val image = Image(id, contentUri, displayName)
                    images += image

                    // For debugging, we'll output the image objects we create to logcat.
                    Log.v(TAG, "Added image: $image")
                }
            }
        }

        Log.v(TAG, "Found ${images.size} images")
        return images
    }

    suspend fun deleteCurrentImage(): Int {
        return deleteImage(_images.value!![currentImagePos].id)
    }

    private suspend fun deleteImage(id: Long): Int = withContext(Dispatchers.IO) {
        Log.v(TAG, "Deleting image with id: $id")

        val selection = "${MediaStore.Images.Media._ID} = ?"

        val selectionArgs = arrayOf("$id")

        val contentUri = ContentUris.withAppendedId(
            getExternalUri(),
            id
        )

        val affectedRows = getApplication<Application>().contentResolver.delete(
            contentUri,
            selection,
            selectionArgs
        )

        Log.v(TAG, "Affected row(s): $affectedRows")

        if (affectedRows == 1) {
            Log.v(TAG, "Deleted successfully")
        } else {
            Log.v(TAG, "Fail to delete image")
        }
        affectedRows
    }

    private fun getExternalUri() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }

    companion object {
        private val TAG = PictureDetailViewModel::class.java.simpleName
    }
}