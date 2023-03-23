package com.phucnguyen.lovereminder.repository

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import com.phucnguyen.lovereminder.viewmodel.PictureViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class PictureRepoImpl @Inject constructor(private val application: Application,
                                          private val ioDispatcher: CoroutineDispatcher)
    : PictureRepo {

    private var _pictures = MutableLiveData<List<Image>>()
    private var contentObserver: ContentObserver? = null

    override fun getImagesStream(): LiveData<List<Image>> = _pictures

    override suspend fun loadPictures(observeChanges: Boolean) {
        withContext(ioDispatcher) {
            val images = queryImages()
            _pictures.postValue(images)
        }

        //register listener to update data whenever media store for uri changes
        if (contentObserver == null && observeChanges) {
            callbackFlow<Boolean> {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        trySend(true)
                    }
                }
                val uri = getExternalUri()
                application.contentResolver.registerContentObserver(uri, true, contentObserver!!)

                awaitClose {
                    contentObserver?.let {
                        application.contentResolver.unregisterContentObserver(it)
                    }
                }
            }.collect {
                withContext(ioDispatcher) {
                    loadPictures(false)
                }
            }
        }
    }

    override suspend fun savePictures(bitmaps: List<Bitmap>) {
        Log.v(TAG, "There is ${bitmaps.size} image(s) to save")
        bitmaps.forEach {
            Log.i(TAG, "Perform save for bitmap: $it")
            saveMediaToStorage(it)
        }
    }

    private suspend fun saveMediaToStorage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val filename = "${PictureFragment.PICTURE_PREFIX}${System.currentTimeMillis()}.jpg"

            var fos: OutputStream? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // getting the contentResolver
                application.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + PictureFragment.PICTURES_FOLDER_NAME)
                        put(MediaStore.Images.Media.WIDTH, bitmap.width)
                        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
                    }

                    val imageUri: Uri? = resolver.insert(getExternalUri(), contentValues)

                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ File.separator + PictureFragment.PICTURES_FOLDER_NAME)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Log.d(TAG, "saved bitmap: $bitmap")
            }
        }
    }

    override suspend fun deleteImages(ids: List<Long>) {
        ids.forEach {
            deleteImage(it)
        }
    }

    override suspend fun deleteImage(id: Long) = withContext(Dispatchers.IO) {
        Log.v(TAG, "Deleting image with id: $id")

        val selection = "${MediaStore.Images.Media._ID} = ?"

        val selectionArgs = arrayOf("$id")

        val contentUri = ContentUris.withAppendedId(
            getExternalUri(),
            id
        )

        val affectedRows = application.contentResolver.delete(
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

    override fun updateList(newList: List<Image>) {
        _pictures.value = newList
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

            application.contentResolver.query(
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

    private fun getExternalUri() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    companion object {
        private val TAG = PictureRepoImpl::class.java.simpleName
    }
}