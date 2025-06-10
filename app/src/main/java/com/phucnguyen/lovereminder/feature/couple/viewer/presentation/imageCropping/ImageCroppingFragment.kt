package com.phucnguyen.lovereminder.feature.couple.viewer.presentation.imageCropping

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.core.base.presentation.BaseFragment
import com.phucnguyen.lovereminder.databinding.FragmentImageCroppingBinding
import com.phucnguyen.lovereminder.feature.couple.common.presentation.enums.ChangeTarget
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_IMAGE_URI = "imageURI"
private const val ARG_CHANGE_TARGET = "changeTarget"

@AndroidEntryPoint
class ImageCroppingFragment :
    BaseFragment<FragmentImageCroppingBinding>(),
    OnCropImageCompleteListener {
    private lateinit var imageURI: String
    private lateinit var changeTarget: ChangeTarget
    private val viewModel: ImageCroppingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageURI = it.getString(ARG_IMAGE_URI)!!
            changeTarget = it.getSerializable(ARG_CHANGE_TARGET) as ChangeTarget
        }
    }

    override fun getClassTag(): String {
        return ImageCroppingFragment::class.java.simpleName
    }

    override fun getViewBindingClass(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentImageCroppingBinding {
        return FragmentImageCroppingBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.toolbar.tb.apply {
            title = "Crop Image"
            inflateMenu(R.menu.menu_image_cropping)
        }

        imageURI?.let {
            val uri = Uri.parse(it)
            binding.cropImageView.setImageUriAsync(uri)
        }

        binding.cropImageView.apply {
            cropShape = CropImageView.CropShape.OVAL
            setAspectRatio(1, 1)
        }
    }

    override fun setViewListener() {
        binding.cropImageView.setOnCropImageCompleteListener(this)

        binding.toolbar.tb.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_crop_image -> {
                    binding.cropImageView.croppedImageAsync()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        viewModel.onImageCropped(changeTarget, result.uriContent!!.toString())
    }

    override fun onDestroyView() {
        binding.cropImageView.setOnCropImageCompleteListener(null)
        super.onDestroyView()
    }

    companion object {
        @JvmStatic fun newInstance(
            changeTarget: ChangeTarget,
            uriPath: String,
        ) =
            ImageCroppingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_URI, uriPath)
                    putSerializable(ARG_CHANGE_TARGET, changeTarget)
                }
            }
    }
}