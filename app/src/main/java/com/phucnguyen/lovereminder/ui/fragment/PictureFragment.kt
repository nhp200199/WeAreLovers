package com.phucnguyen.lovereminder.ui.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.FragmentPictureBinding
import com.phucnguyen.lovereminder.ui.activity.FullScreenPicActivity
import com.phucnguyen.lovereminder.ui.adapter.ImageAdapter
import com.phucnguyen.lovereminder.viewmodel.PictureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.*

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PictureFragment : Fragment(), View.OnClickListener {
    private var adapter: ImageAdapter? = null
    private var binding: FragmentPictureBinding? = null
    private val viewModel: PictureViewModel by viewModels()
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

        adapter?.listener = object : ImageAdapter.Listener {
            override fun onItemClicked(position: Int) {
                if (!viewModel.isChoosingImageToDelete()) {
                    viewFullScreenPicture(position)
                } else {
                    viewModel.togglePictureDeleteStatus(position)
                }
            }

            override fun onItemLongClicked(position: Int) {
                viewModel.togglePictureDeleteStatus(position)
            }
        }
        binding!!.rcvPictures.apply {
            adapter = this@PictureFragment.adapter
            layoutManager = GridLayoutManager(context!!, 3)
        }

        viewModel.pictures.observe(viewLifecycleOwner) { images ->
            if (images.isEmpty()) {
                binding!!.linearLayout.visibility = View.VISIBLE
                binding!!.rcvPictures.visibility = View.GONE
            } else {
                binding!!.linearLayout.visibility = View.GONE
                binding!!.rcvPictures.visibility = View.VISIBLE
                adapter!!.images = images
            }
        }

        viewModel.isChoosingImageToDeleteStream.observe(viewLifecycleOwner) {isChoosing ->
            if (isChoosing) {
                binding!!.fabAddImage.hide()
            } else {
                binding!!.fabAddImage.show()
            }
            requireActivity().invalidateOptionsMenu()
        }

        binding!!.fabAddImage.setOnClickListener(this)
        if (!haveReadStoragePermission()) {
            requestReadStoragePermission()
        } else {
            viewModel.loadImages()
        }
        return v
    }

    private fun viewFullScreenPicture(position: Int) {
        val intent = Intent(requireActivity(), FullScreenPicActivity::class.java).apply {
            putExtra(FullScreenPicActivity.EXTRA_PICTURE_POS, position)
        }
        startActivity(intent)
    }

    private fun haveWriteStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                } else true
    }

    private fun haveReadStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context!!.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PERMISSION_GRANTED
        } else {
            context!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        }
    }

    private fun requestReadStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
        ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST)
    }

    private fun requestReadAndWriteStoragePermission() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        requestPermissions(permissions.toTypedArray(), READ_AND_WRITE_EXTERNAL_STORAGE_REQUEST)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_remove, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val deleteMenuItem = menu.findItem(R.id.action_delete_picture)
        val cancelMenuItem = menu.findItem(R.id.action_cancel)
        super.onPrepareOptionsMenu(menu)
        deleteMenuItem.isVisible = viewModel.isChoosingImageToDelete()
        cancelMenuItem.isVisible = viewModel.isChoosingImageToDelete()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_picture -> {
                showPopupConfirmDeletion()
                true
            }
            R.id.action_cancel -> {
                viewModel.clearAllPendingDeletePicture()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupConfirmDeletion() {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setMessage(String.format("Bạn muốn xóa %d ảnh đã chọn?", viewModel.numberOfPendingImages()))
            .setPositiveButton("Có", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                    viewModel.deletePendingImages()
                }
            })
            .setNegativeButton("Không") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            //when user selects multiple images, data.getClipData will not null
            val bitmaps  = mutableListOf<Bitmap>()
            if (data!!.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    clipData.getItemAt(i).uri?.let {
                        val bm = getBitmap(it, requireActivity().contentResolver)
                        if (bm != null) {
                            bitmaps.add(bm)
                        } else {
                            Toast.makeText(requireContext(), "Error when getting image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else if (data.data != null) {
                val uri = data.data!!
                val bm = getBitmap(uri, requireActivity().contentResolver)
                if (bm != null) {
                    bitmaps.add(bm)
                } else {
                    Toast.makeText(requireContext(), "Error when getting image", Toast.LENGTH_SHORT).show()
                }
            }
            lifecycleScope.launch {
                viewModel.saveImages(bitmaps)
            }
        }
    }

    private fun getBitmap(file: Uri, cr: ContentResolver): Bitmap?{
        var bitmap: Bitmap ?= null
        try {
            val inputStream = cr.openInputStream(file)
            bitmap = BitmapFactory.decodeStream(inputStream)
            // close stream
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } catch (e: FileNotFoundException){}
        return bitmap
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabAddImage -> chooseImagesFromGallery()
        }
    }

    private fun chooseImagesFromGallery() {
        if (haveReadStoragePermission() && haveWriteStoragePermission()) {
            val intent = Intent().apply {
                action = Intent.ACTION_OPEN_DOCUMENT
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), REQUEST_CHOOSE_IMAGE)
        } else {
            requestReadAndWriteStoragePermission()
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
            READ_AND_WRITE_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PERMISSION_GRANTED
                    && grantResults[1] == PERMISSION_GRANTED
                ) {
                    chooseImagesFromGallery()
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
    }

    companion object {
        private const val RESULT_OK = -1
        private const val READ_EXTERNAL_STORAGE_REQUEST = 113
        const val READ_AND_WRITE_EXTERNAL_STORAGE_REQUEST = 100
        const val REQUEST_CHOOSE_IMAGE = 443
        const val LOG_TAG = "PictureFragment"
        const val PICTURES_FOLDER_NAME = "saved-pictures"
        const val PICTURE_PREFIX = "PNLovereminder-"
    }
}