package com.phucnguyen.lovereminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.databinding.FragmentMainBinding
import com.phucnguyen.lovereminder.ui.activity.SettingActivity
import com.phucnguyen.lovereminder.ui.uiState.UserInfoUiState
import com.phucnguyen.lovereminder.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private val viewModel: MainFragmentViewModel by viewModels()
    var height = 0
    var width = 0
    private lateinit var coupleDate: String
    override fun onDestroy() {
        Log.d("Tag", "Main Frag Destroyed")
        super.onDestroy()
    }

    override fun onStop() {
        Log.d("Tag", "Main Frag Stopped")
        super.onStop()
    }

    override fun onResume() {
        Log.d("Tag", "Main Frag Resumed")
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Tag", "Main Frag created")
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        Log.d("Tag", "Main Frag Destroyed View")
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        binding = FragmentMainBinding.bind(v)
        val displayMetrics = DisplayMetrics()
        requireActivity() .windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        setHasOptionsMenu(true)
        val zoomin = AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
        binding!!.imgHeart.startAnimation(zoomin)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navigateSettingScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateSettingScreen() {
        startActivity(Intent(requireContext(), SettingActivity::class.java))
    }

    private fun setUserInfo(userInfoState: UserInfoUiState) {
        binding!!.tvYourName.text = userInfoState.yourName
        binding!!.tvYourFrName.text = userInfoState.yourFrName

        val calendar = Calendar.getInstance()
        var dateStartString = userInfoState.coupleDate
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateStart = simpleDateFormat.parse(dateStartString)
        val dateEndString = simpleDateFormat.format(calendar.time)
        val dateEnd = simpleDateFormat.parse(dateEndString)
        val diff = dateEnd.time - dateStart.time
        binding!!.tvDayCount.text =
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                .toString()

        Glide.with(requireActivity()  )
            .load(userInfoState.yourImage)
            .into(binding!!.profileImage)
        Glide.with(requireActivity()  )
            .load(userInfoState.yourFrImage)
            .into(binding!!.friendProfileImage)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfoUiStateFlow.collect {
                    coupleDate = it.coupleDate
                    setUserInfo(it)
                }
            }
        }
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}