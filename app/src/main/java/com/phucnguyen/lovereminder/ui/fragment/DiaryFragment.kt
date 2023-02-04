package com.phucnguyen.lovereminder.ui.fragment

    import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.FragmentDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.ui.activity.CreateDiaryActivity
import com.phucnguyen.lovereminder.ui.activity.DiaryActivity
import com.phucnguyen.lovereminder.ui.adapter.DiaryAdapter
import com.phucnguyen.lovereminder.viewmodel.DiaryFragmentViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class DiaryFragment : Fragment() {
    private lateinit var adapter: DiaryAdapter
    private lateinit var rcvDiaries: RecyclerView
    private lateinit var tvNothing: TextView
    private lateinit var svDiary: SearchView
    private lateinit var fabAddDiary: FloatingActionButton
    private var binding: FragmentDiaryBinding? = null
    private var interstitialAd: InterstitialAd? = null
    private lateinit var viewModel: DiaryFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        Log.d("Tag", "Dia Frag created")
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        Log.d("Tag", "Dia Frag Destroyed View")
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_diary, container, false)
        binding = FragmentDiaryBinding.bind(v)
        viewModel = ViewModelProvider(requireActivity()).get(DiaryFragmentViewModel::class.java)
        loadAd()
        connectViews(binding!!)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadDiaries()
            }
        }
        return v
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        activity?.let {
            InterstitialAd.load(
                it,
                AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        this@DiaryFragment.interstitialAd = interstitialAd
                        Log.i(TAG, "onAdLoaded")
                        Toast.makeText(activity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                        interstitialAd.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    this@DiaryFragment.interstitialAd = null
                                    Log.d("TAG", "The ad was dismissed.")
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    // Called when fullscreen content failed to show.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    this@DiaryFragment.interstitialAd = null
                                    Log.d("TAG", "The ad failed to show.")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    this@DiaryFragment.interstitialAd = null
                                    Log.d("TAG", "The ad was shown.")
                                }
                            }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.message)
                        interstitialAd = null
                        val error = String.format(
                            "domain: %s, code: %d, message: %s",
                            loadAdError.domain, loadAdError.code, loadAdError.message
                        )
                        Toast.makeText(
                            activity, "onAdFailedToLoad() with error: $error", Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun connectViews(binding: FragmentDiaryBinding) {
        rcvDiaries = binding.rcvDiaries
        tvNothing = binding.tvNothing
        fabAddDiary = binding.fabAddDiary
        adapter = DiaryAdapter(activity!!)
        adapter.setListener(object : DiaryAdapter.Listener {
            override fun onDiaryLongClickListener(diary: Diary, v: View) {
                val popupMenu = PopupMenu(
                    context!!, v
                )
                popupMenu.inflate(R.menu.remove_item_listview)
                popupMenu.setOnMenuItemClickListener {
                    lifecycleScope.launch {
                        val deleteResult = viewModel.deleteDiary(diary)
                        if (deleteResult > 0) {
                            Toast.makeText(requireContext(), getString(R.string.toast_msg_deleted_diary), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.toast_msg_fail_delete_diary), Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                popupMenu.show()
            }

            override fun onDiaryClickListener(diary: Diary) {
                val intent = Intent(activity, DiaryActivity::class.java)
                intent.putExtra("id", diary.id)
                startActivity(intent)
            }
        })
        rcvDiaries.adapter = adapter
        rcvDiaries.layoutManager = LinearLayoutManager(activity)
        fabAddDiary.setOnClickListener {
            val intent = Intent(requireActivity(), CreateDiaryActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun loadDiaries() {
        viewModel.getAllDiaries().collect() { diaries ->
            if (diaries.isEmpty()) {
                tvNothing.visibility = View.VISIBLE
                rcvDiaries.visibility = View.INVISIBLE
            } else {
                tvNothing.visibility = View.INVISIBLE
                rcvDiaries.visibility = View.VISIBLE
                adapter.submitList(diaries)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_diary, menu)
        // Associate searchable configuration with the SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        svDiary = menu.findItem(R.id.action_search_diary).actionView as SearchView
        svDiary.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            maxWidth = Int.MAX_VALUE
            //update the query in search view
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            //remove search icon
            setIconifiedByDefault(false)
            isIconified = false // this is important to hide the search hint icon
        }
        val icon = svDiary.findViewById<View>(androidx.appcompat.R.id.search_mag_icon) as ImageView
        //        icon.setVisibility(View.GONE);
        icon.setImageDrawable(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_diary -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val TAG = DiaryFragment::class.java.simpleName
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}