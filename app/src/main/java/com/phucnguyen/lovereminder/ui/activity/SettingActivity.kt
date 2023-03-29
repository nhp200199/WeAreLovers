package com.phucnguyen.lovereminder.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.ActivitySettingBinding
import com.phucnguyen.lovereminder.utils.hideKeyboard
import com.phucnguyen.lovereminder.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val viewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        setupListeners()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userNameFlow.collect {
                    binding.tvYourName.text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userFriendNameFlow.collect {
                    binding.tvYourFriendName.text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backgroundPictureFlow.collect {
                    Glide.with(binding.ivBackgroundPicture)
                        .load(it)
                        .into(binding.ivBackgroundPicture)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.yourAvatarFlow.collect {
                    Glide.with(binding.civYourAvatar)
                        .load(it)
                        .into(binding.civYourAvatar)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.yourFriendAvatarFlow.collect {
                    Glide.with(binding.civYourFriendAvatar)
                        .load(it)
                        .into(binding.civYourFriendAvatar)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEditingYourNameFlow.collect { isEditing ->
                    if (isEditing) {
                        binding.tvActionEditYourName.text = getString(R.string.txt_action_done_edit_name)

                        binding.edtEditYourName.visibility = View.VISIBLE
                        binding.edtEditYourName.setText(binding.tvYourName.text)
                        binding.tvYourName.visibility = View.GONE
                    } else {
                        binding.tvActionEditYourName.text = getString(R.string.txt_action_edit_name)

                        binding.edtEditYourName.visibility = View.GONE
                        binding.tvYourName.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEditingYourFriendNameFlow.collect { isEditing ->
                    if (isEditing) {
                        binding.tvActionEditYourFriendName.text = getString(R.string.txt_action_done_edit_name)

                        binding.edtEditYourFriendName.visibility = View.VISIBLE
                        binding.edtEditYourFriendName.setText(binding.tvYourFriendName.text)
                        binding.tvYourFriendName.visibility = View.GONE
                    } else {
                        binding.tvActionEditYourFriendName.text = getString(R.string.txt_action_edit_name)

                        binding.edtEditYourFriendName.visibility = View.GONE
                        binding.tvYourFriendName.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun setupListeners() {
        binding.tvActionEditYourName.setOnClickListener {
            if (!viewModel.isEditingYourName) {
                viewModel.startEditYourName()
            } else {
                hideKeyboard(this)
                viewModel.stopEditYourName()
                viewModel.changeYourName(binding.edtEditYourName.text.toString())
            }
        }

        binding.tvActionEditYourFriendName.setOnClickListener {
            if (!viewModel.isEditingYourFriendName) {
                viewModel.startEditYourFriendName()
            } else {
                hideKeyboard(this)
                viewModel.stopEditYourFriendName()
                viewModel.changeYourFriendName(binding.edtEditYourFriendName.text.toString())
            }
        }
    }
}