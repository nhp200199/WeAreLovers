package com.phucnguyen.lovereminder.ui.fragment

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.FragmentPictureBinding
import com.phucnguyen.lovereminder.ui.adapter.ImageAdapter
import com.phucnguyen.lovereminder.viewmodel.PictureViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class PictureFragment : Fragment(), View.OnClickListener {
    private var adapter: ImageAdapter? = null
    private var binding: FragmentPictureBinding? = null
    private lateinit var viewModel: PictureViewModel
    private var numOfPicturesTobeDeleted = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //workaround for saving adapter's state
        adapter = ImageAdapter(requireActivity())
    }

    override fun onDestroyView() {
        Log.d("Tag", "Pic Frag Destroyed View")
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_picture, container, false)
        binding = FragmentPictureBinding.bind(v)
        viewModel = ViewModelProvider(requireActivity()).get(
            PictureViewModel::class.java
        )
        binding!!.rcvPictures.apply {
            adapter = this@PictureFragment.adapter
            layoutManager = GridLayoutManager(context!!, 3)
        }

        viewModel.images.observe(viewLifecycleOwner) { images ->
            if (images.isEmpty()) {
                binding!!.linearLayout.visibility = View.VISIBLE
                binding!!.rcvPictures.visibility = View.GONE
            } else {
                binding!!.linearLayout.visibility = View.GONE
                binding!!.rcvPictures.visibility = View.VISIBLE
                adapter!!.images = images
            }
        }
//        val onItemClickListener = OnItemClickListener { parent, view, position, id ->
//            if (numOfPicturesTobeDeleted == 0) {
//                val intent = Intent(activity, FullScreenPicActivity::class.java)
//                intent.putExtra("position", position)
//                intent.putExtra("uri", images[position].uri)
//                startActivity(intent)
//            } else {
//                if (adapter!!.toggleImagePositionToDelete(position)) numOfPicturesTobeDeleted++ else {
//                    numOfPicturesTobeDeleted--
//                    //if numOfPicturesTobeDeleted ==0 -> hide menu action
//                    if (numOfPicturesTobeDeleted == 0) requireActivity().invalidateOptionsMenu()
//                }
//            }
//        }
//        gvPictures!!.onItemClickListener = onItemClickListener
//        gvPictures!!.onItemLongClickListener =
//            OnItemLongClickListener { parent, view, position, id ->
//                if (numOfPicturesTobeDeleted == 0) {
//                    numOfPicturesTobeDeleted++
//                    adapter!!.toggleImagePositionToDelete(position)
//                    requireActivity().invalidateOptionsMenu()
//                } else {
//                    //toggle image selection for each long click event
//                    if (adapter!!.toggleImagePositionToDelete(position)) numOfPicturesTobeDeleted++ else {
//                        numOfPicturesTobeDeleted--
//                        //if numOfPicturesTobeDeleted ==0 -> hide menu action
//                        if (numOfPicturesTobeDeleted == 0) {
//                            requireActivity().invalidateOptionsMenu()
//                        }
//                    }
//                }
//                true
//            }
        binding!!.fabAddImage.setOnClickListener(this)
        if (!haveStoragePermission()) {
            requestStoragePermission()
        } else {
            viewModel.loadImages()
        }
        return v
    }

    private fun haveStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context!!.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PERMISSION_GRANTED
                            && context!!.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PERMISSION_GRANTED
                            && context!!.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PERMISSION_GRANTED
                } else {
                    context!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                }
    }

    private fun requestStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (numOfPicturesTobeDeleted > 0) {
            outState.putInt("num_of_pictures_to_be_deleted", numOfPicturesTobeDeleted)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_remove, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val deleteMenuItem = menu.findItem(R.id.action_delete_picture)
        val cancelMenuItem = menu.findItem(R.id.action_cancel)
        super.onPrepareOptionsMenu(menu)
        if (numOfPicturesTobeDeleted == 0) {
            deleteMenuItem.isVisible = false
            cancelMenuItem.isVisible = false
        } else {
            deleteMenuItem.isVisible = true
            cancelMenuItem.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_picture -> {
                showPopupConfirmDeletion()
                true
            }
            R.id.action_cancel -> {
                numOfPicturesTobeDeleted = 0
                activity!!.invalidateOptionsMenu()
                //remove selection's effect on selected items
                adapter!!.resetTracker()
                adapter!!.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupConfirmDeletion() {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setMessage(String.format("Bạn muốn xóa %d ảnh đã chọn?", numOfPicturesTobeDeleted))
            .setPositiveButton("Có", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                    //delete the selected pictures
//                    removePic()
                    //reset deletion-related things and update menu actions
                    numOfPicturesTobeDeleted = 0
                    adapter!!.resetTracker()
                    activity!!.invalidateOptionsMenu()
                }

//                private fun removePic() {
//                    val indexesOfImagesToDelete = adapter!!.imagesToDelete
//                    val pictureFolder = File(
//                        context!!.getExternalFilesDir(
//                            Environment.DIRECTORY_PICTURES
//                        ), PICTURES_FOLDER_NAME
//                    )
//                    for (i in indexesOfImagesToDelete.size() - 1 downTo 0) {
//                        //To handle ArrayIndexOutOfBound, we delete the image with higher index first
//                        val indexInImageList = indexesOfImagesToDelete.keyAt(i)
//                        val filePathName = images[indexInImageList].uri!!.lastPathSegment
//                        images.removeAt(indexInImageList)
//                        //delete file in external dir
//                        val fileToBeDeleted = File(pictureFolder, filePathName)
//                        fileToBeDeleted.delete()
//                    }
//                    Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show()
//                    pictureViewModel!!.setImages(images)
//                }
            })
            .setNegativeButton("Không") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
//            //when user selects multiple images, data.getClipData will not null
//            if (data!!.clipData != null) {
//                val clipData = data.clipData
//                for (i in 0 until clipData!!.itemCount) {
//                    //TODO: load image's thumbnail, not the whole image
//                    val uri = clipData.getItemAt(i).uri
//                    val image = Image()
//                    image.uri = uri
//                    images.add(0, image)
//                    saveImageToExternalDir(uri)
//                }
//            } else if (data.data != null) {
//                //TODO: load image's thumbnail, not the whole image
//                val uri = data.data
//                val image = Image()
//                image.uri = uri
//                images.add(0, image)
//                saveImageToExternalDir(uri)
//            }
//            pictureViewModel!!.setImages(images)
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun saveImageToExternalDir(uri: Uri?) {
        Observable.create<Boolean> { emitter ->
            try {
                //saved the selected picture to external app-specific files
                val calendar = Calendar.getInstance()
                val createdTime = calendar.timeInMillis //make each file unique
                val file = File(
                    context!!.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                    ), PICTURES_FOLDER_NAME
                )
                if (!file.exists() && !file.mkdir()) {
                    Log.e(LOG_TAG, "Directory not created")
                    emitter.onError(Throwable("ERROR"))
                } else {
                    val imageFileToSave = File(file, "$createdTime.png")
                    try {
                        if (imageFileToSave.createNewFile()) {
                            val outputStream: OutputStream = FileOutputStream(imageFileToSave)
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                            emitter.onComplete()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        emitter.onError(e)
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result: Boolean -> Log.d(LOG_TAG, result.toString()) }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabAddImage -> if (numOfPicturesTobeDeleted == 0) chooseImagesFromGallery() else Toast.makeText(
                requireContext(),
                "Vui lòng hoàn thành thao tác khác trước",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun chooseImagesFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQ
            )
        } else {
            val intent = Intent()
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), REQUEST_CHOOSE_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    viewModel.loadImages()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Bạn cần cho phép để truy cập ảnh",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
//        if (requestCode == READ_EXTERNAL_STORAGE_REQ && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) chooseImagesFromGallery() else Toast.makeText(
//                requireContext(),
//                "Bạn cần cho phép để truy cập ảnh",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST)
    }

    companion object {
        private const val RESULT_OK = -1
        private const val READ_EXTERNAL_STORAGE_REQUEST = 113
        const val READ_EXTERNAL_STORAGE_REQ = 100
        const val REQUEST_CHOOSE_IMAGE = 443
        const val LOG_TAG = "PictureFragment"
        const val PICTURES_FOLDER_NAME = "saved-pictures"
        const val PICTURE_PREFIX = "PNLovereminder-"
    }
}