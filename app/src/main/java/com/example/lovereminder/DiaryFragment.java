package com.example.lovereminder;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovereminder.databinding.FragmentDiaryBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
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

    private DiaryDao mDiaryDao;
    private FragmentDiaryBinding binding;

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDiaryDao = AppDatabase.getInstance(requireContext()).getDiaryDao();
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
        connectViews(binding);

        loadDiaries();
        return v;
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

        mDiaryDao.getAllDiaries()
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
                        error -> error.printStackTrace());
    }
}
