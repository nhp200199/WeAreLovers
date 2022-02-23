package com.phucnguyen.lovereminder.ui.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phucnguyen.lovereminder.database.AppDatabase;
import com.phucnguyen.lovereminder.model.Diary;
import com.phucnguyen.lovereminder.ui.adapter.DiaryAdapter;
import com.phucnguyen.lovereminder.database.DiaryDao;
import com.phucnguyen.lovereminder.R;
import com.phucnguyen.lovereminder.databinding.FragmentDiaryBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.phucnguyen.lovereminder.ui.activity.CreateDiaryActivity;
import com.phucnguyen.lovereminder.ui.activity.DiaryActivity;

import org.jetbrains.annotations.NotNull;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends Fragment {
    public static final String TAG = DiaryFragment.class.getSimpleName();

    private DiaryAdapter adapter;
    private RecyclerView rcvDiaries;
    private TextView tvNothing;
    private FloatingActionButton fabAddDiary;
    private androidx.appcompat.widget.SearchView svDiary;

    private DiaryDao mDiaryDao;
    private FragmentDiaryBinding binding;
    private Disposable mGetAllDiaryDisposable;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd interstitialAd;

    private int touchesToOpenAd = 5;

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDiaryDao = AppDatabase.getInstance(requireContext()).getDiaryDao();
        setHasOptionsMenu(true);
        Log.d("Tag", "Dia Frag created");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.d("Tag", "Dia Frag Destroyed View");
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_diary, container, false);
        binding = FragmentDiaryBinding.bind(v);
        loadAd();

        connectViews(binding);

        loadDiaries();

        return v;
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                getActivity(),
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        DiaryFragment.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        Toast.makeText(getActivity(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        DiaryFragment.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        DiaryFragment.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        DiaryFragment.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                getActivity(), "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void connectViews(FragmentDiaryBinding binding) {
        rcvDiaries = binding.rcvDiaries;
        tvNothing = binding.tvNothing;
        fabAddDiary = binding.fabAddDiary;

        adapter = new DiaryAdapter(getActivity());
        adapter.setListener(new DiaryAdapter.Listener() {
            @Override
            public void onDiaryLongClickListener(final Diary diary, View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.remove_item_listview);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mDiaryDao.deleteDiary(diary)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {
                                        Log.d(TAG, "onSubscribe");
                                    }

//                                    @Override
//                                    public void onSuccess(@NotNull Integer integer) {
//                                        Log.d(TAG, "onComplete " + integer);
//                                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
//                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(TAG, "onComplete");
                                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {
                                        Log.d(TAG, "onError");
                                    }
                                });
                        return true;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onDiaryClickListener(Diary diary) {
                Intent intent = new Intent(getActivity(), DiaryActivity.class);
                intent.putExtra("id", diary.getId());
                startActivity(intent);
            }
        });
        rcvDiaries.setAdapter(adapter);
        rcvDiaries.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CreateDiaryActivity.class);
                startActivity(intent);
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
        });
    }

    private void loadDiaries() {
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

        mGetAllDiaryDisposable = mDiaryDao.getAllDiaries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(diaries -> {
                            if (diaries.size() == 0) {
                                tvNothing.setVisibility(View.VISIBLE);
                                rcvDiaries.setVisibility(View.INVISIBLE);
                            } else {
                                tvNothing.setVisibility(View.INVISIBLE);
                                rcvDiaries.setVisibility(View.VISIBLE);

                                adapter.submitList(diaries);
                            }
                        },
                        error -> Log.e(TAG, error.getMessage()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_diary, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        svDiary = (SearchView) menu.findItem(R.id.action_search_diary).getActionView();
        svDiary.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        svDiary.setMaxWidth(Integer.MAX_VALUE);
        //update the query in search view
        svDiary.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //remove search icon
        svDiary.setIconifiedByDefault(false);
        svDiary.setIconified(false); // this is important to hide the search hint icon
        ImageView icon = (ImageView) svDiary.findViewById(androidx.appcompat.R.id.search_mag_icon);
//        icon.setVisibility(View.GONE);
        icon.setImageDrawable(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_diary:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGetAllDiaryDisposable != null)
            mGetAllDiaryDisposable.dispose();
    }
}
