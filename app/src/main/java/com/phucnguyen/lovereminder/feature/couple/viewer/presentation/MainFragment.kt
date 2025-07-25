package com.phucnguyen.lovereminder.feature.couple.viewer.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.core.base.presentation.BaseFragment
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_FRIEND_NAME
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_NAME
import com.phucnguyen.lovereminder.core.utils.parseDateTimestamps
import com.phucnguyen.lovereminder.databinding.FragmentMainBinding
import com.phucnguyen.lovereminder.feature.couple.common.presentation.enums.ChangeTarget
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.imageCropping.ImageCroppingFragment
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.state.UserInfoUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    private val viewModel: MainFragmentViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            if (viewModel.getTarget() != null) {
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_container,
                        ImageCroppingFragment.newInstance(
                            viewModel.getTarget()!!,
                            uri.toString())
                    )
                    addToBackStack(null)

                    commit()
                }
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(getClassTag(), "onOptionsItemSelected: ${item.itemId}")
        return when (item.itemId) {
            R.id.menu_action_edit_couple_data -> {
                viewModel.setIsEditingCoupleData(isVisible)
                true
            }
            R.id.menu_action_edit_background -> {
                viewModel.targetChanged(ChangeTarget.BACKGROUND)
                openImagePicker()
                true
            }
            R.id.action_save_couple_data -> {
                viewModel.setIsEditingCoupleData(false)
                viewModel.saveCoupleData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun enableCoupleDataEditor(visible: Boolean) {
        binding.ibEditCoupleDate.isVisible = visible
        binding.ibEditYourName.isVisible = visible
        binding.ibEditYourImage.isVisible = visible
        binding.ibEditYourPartnerName.isVisible = visible
        binding.ibEditYourPartnerImage.isVisible = visible
    }

    private fun setUserInfo(userInfoState: UserInfoUiState) {
        binding.tvYourName.text = userInfoState.yourName
        binding.tvYourFrName.text = userInfoState.yourFrName

        binding.tvDayCount.text = userInfoState.coupleDate

        Glide.with(requireActivity())
            .load(userInfoState.yourImage)
            .into(binding.profileImage)
        Glide.with(requireActivity())
            .load(userInfoState.yourFrImage)
            .into(binding.friendProfileImage)
    }

    override fun getClassTag(): String {
        return MainFragment::class.java.simpleName
    }

    override fun getViewBindingClass(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        setHasOptionsMenu(true)

        val zoomin = AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
        binding.imgHeart.startAnimation(zoomin)
    }

    override fun setViewListener() {
        binding.ibEditYourPartnerImage.setOnClickListener {
            viewModel.targetChanged(ChangeTarget.YOUR_PARTNER)
            openImagePicker()
        }

        binding.ibEditYourImage.setOnClickListener {
            viewModel.targetChanged(ChangeTarget.YOU)
            openImagePicker()
        }

        binding.ibEditYourName.setOnClickListener {
            showDialogChangeName(PREF_YOUR_NAME, binding.tvYourName.text.toString())
        }

        binding.ibEditYourPartnerName.setOnClickListener {
            showDialogChangeName(PREF_YOUR_FRIEND_NAME, binding.tvYourFrName.text.toString())
        }

        binding.ibEditCoupleDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun openImagePicker() {
        if (viewModel.getTarget() != null) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(),
            android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight,
            { view, year, month, dayOfMonth -> //because the month is counted from 0
                var month = month
                month = month + 1
                val date = "$dayOfMonth/$month/$year"
                viewModel.setCoupleDate(parseDateTimestamps(date))
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun showDialogChangeName(target: String, currentValue: String) {
        val bundle = Bundle()
        bundle.putString("target", target)
        bundle.putString("name", currentValue)

        val dialogFragment = ChangeNameDialogFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "ChangeNameDialogFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfoUiStateFlow.collect {
                    setUserInfo(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEditingCoupleDataFlow.collect {
                    invalidateOptionsMenu(requireActivity())
                    enableCoupleDataEditor(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelEditCoupleData()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = !viewModel.isEditingCoupleData()
        menu.findItem(R.id.action_save_couple_data).isVisible = viewModel.isEditingCoupleData()
        super.onPrepareOptionsMenu(menu)
    }
}