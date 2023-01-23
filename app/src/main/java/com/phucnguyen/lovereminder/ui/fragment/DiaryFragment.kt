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
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.databinding.FragmentDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.ui.activity.CreateDiaryActivity
import com.phucnguyen.lovereminder.ui.activity.DiaryActivity
import com.phucnguyen.lovereminder.ui.adapter.DiaryAdapter
import com.phucnguyen.lovereminder.ui.fragment.DiaryFragment
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
class DiaryFragment : Fragment() {
    private var adapter: DiaryAdapter? = null
    private var rcvDiaries: RecyclerView? = null
    private var tvNothing: TextView? = null
    private var fabAddDiary: FloatingActionButton? = null
    private var svDiary: SearchView? = null
    private var mDiaryDao: DiaryDao? = null
    private var binding: FragmentDiaryBinding? = null
    private var mGetAllDiaryDisposable: Disposable? = null
    private var interstitialAd: InterstitialAd? = null
    private val touchesToOpenAd = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        mDiaryDao = getInstance(requireContext()).diaryDao
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
        loadAd()
        connectViews(binding!!)
        loadDiaries()
        return v
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
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

    private fun connectViews(binding: FragmentDiaryBinding) {
        rcvDiaries = binding.rcvDiaries
        tvNothing = binding.tvNothing
        fabAddDiary = binding.fabAddDiary
        adapter = DiaryAdapter(activity!!)
        adapter!!.setListener(object : DiaryAdapter.Listener {
            override fun onDiaryLongClickListener(diary: Diary?, v: View?) {
                val popupMenu = PopupMenu(
                    context!!, v!!
                )
                popupMenu.inflate(R.menu.remove_item_listview)
                popupMenu.setOnMenuItemClickListener {
                    mDiaryDao!!.deleteDiary(diary)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : CompletableObserver {
                            override fun onSubscribe(d: Disposable) {
                                Log.d(TAG, "onSubscribe")
                            }

                            //                                    @Override
                            //                                    public void onSuccess(@NotNull Integer integer) {
                            //                                        Log.d(TAG, "onComplete " + integer);
                            //                                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            //                                    }
                            override fun onComplete() {
                                Log.d(TAG, "onComplete")
                                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onError(e: Throwable) {
                                Log.d(TAG, "onError")
                            }
                        })
                    true
                }
                popupMenu.show()
            }

            override fun onDiaryClickListener(diary: Diary?) {
                val intent = Intent(activity, DiaryActivity::class.java)
                intent.putExtra("id", diary!!.id)
                startActivity(intent)
            }
        })
        rcvDiaries!!.adapter = adapter
        rcvDiaries!!.layoutManager = LinearLayoutManager(activity)
        //        lvDiaries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), DiaryActivity.class);
//                intent.putExtra("date", diaries.get(position).getDate());
//                intent.putExtra("content", diaries.get(position).getContent());
//                intent.putExtra("lst_position", position);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        });
//
        fabAddDiary!!.setOnClickListener {
            val intent = Intent(requireActivity(), CreateDiaryActivity::class.java)
            startActivity(intent)
            //                touchesToOpenAd --;
//                if (touchesToOpenAd == 1) {
//                    loadAd();
//                }
//                else if (touchesToOpenAd == 0) {
//                    if (interstitialAd != null) {
//                        interstitialAd.show(getActivity());
//                        touchesToOpenAd = (int) (Math.random() * 4 + 2); //range from 2 to 5
//                        Toast.makeText(getActivity(), "New touches set: " + touchesToOpenAd, Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        touchesToOpenAd = 1;
//                        loadAd();
//                    }
//                }
        }
    }

    private fun loadDiaries() {
//        mDiaryDao.getAllDiaries().observe(getViewLifecycleOwner(), new Observer<List<Diary>>() {
//            @Override
//            public void onChanged(List<Diary> diaries) {
//                if (diaries.size() == 0) {
//                    tvNothing.setVisibility(View.VISIBLE);
//                    rcvDiaries.setVisibility(View.INVISIBLE);
//                } else {
//                    tvNothing.setVisibility(View.INVISIBLE);
//                    rcvDiaries.setVisibility(View.VISIBLE);
//
//                    adapter.submitList(diaries);
//                }
//            }
//        });
        mGetAllDiaryDisposable = mDiaryDao!!.allDiaries
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { diaries: List<Diary?> ->
                    if (diaries.size == 0) {
                        tvNothing!!.visibility = View.VISIBLE
                        rcvDiaries!!.visibility = View.INVISIBLE
                    } else {
                        tvNothing!!.visibility = View.INVISIBLE
                        rcvDiaries!!.visibility = View.VISIBLE
                        adapter!!.submitList(diaries)
                    }
                }
            ) { error: Throwable -> Log.e(TAG, error.message!!) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_diary, menu)
        // Associate searchable configuration with the SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        svDiary = menu.findItem(R.id.action_search_diary).actionView as SearchView?
        svDiary!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        svDiary!!.maxWidth = Int.MAX_VALUE
        //update the query in search view
        svDiary!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
        //remove search icon
        svDiary!!.setIconifiedByDefault(false)
        svDiary!!.isIconified = false // this is important to hide the search hint icon
        val icon =
            svDiary!!.findViewById<View>(androidx.appcompat.R.id.search_mag_icon) as ImageView
        //        icon.setVisibility(View.GONE);
        icon.setImageDrawable(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_diary -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mGetAllDiaryDisposable != null) mGetAllDiaryDisposable!!.dispose()
    }

    companion object {
        val TAG = DiaryFragment::class.java.simpleName
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}