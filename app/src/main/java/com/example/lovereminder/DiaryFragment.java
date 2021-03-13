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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends Fragment {

    private DiaryAdapter adapter;
    private RecyclerView rcvDiaries;
    private TextView tvNothing;
    private FloatingActionButton fabAddDiary;

    private DiaryDao mDiaryDao;

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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_diary, container, false);
        connectViews(v);

        loadDiaries();
        return v;
    }

    private void connectViews(View v) {
        rcvDiaries = (RecyclerView) v.findViewById(R.id.rcv_diaries);
        tvNothing = (TextView) v.findViewById(R.id.tv_nothing);
        fabAddDiary = (FloatingActionButton) v.findViewById(R.id.fabAddDiary);

        adapter = new DiaryAdapter(getActivity());
        adapter.setListener(new DiaryAdapter.Listener() {
            @Override
            public void onDiaryLongClickListener(final Diary diary, View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.remove_item_listview);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        new DeleteDiaryAsync(mDiaryDao).execute(diary);
                        return true;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onDiaryClickListener(Diary diary) {
                Intent intent = new Intent(getActivity(), DiaryActivity.class);
                intent.putExtra("id", diary.getId());
                intent.putExtra("date", diary.getDate());
                intent.putExtra("content", diary.getContent());
                startActivity(intent);
                getActivity().finish();
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
        mDiaryDao.getAllDiaries().observe(getViewLifecycleOwner(), new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                if (diaries.size() == 0) {
                    tvNothing.setVisibility(View.VISIBLE);
                    rcvDiaries.setVisibility(View.INVISIBLE);
                } else {
                    tvNothing.setVisibility(View.INVISIBLE);
                    rcvDiaries.setVisibility(View.VISIBLE);

                    adapter.submitList(diaries);
                }
            }
        });
    }

    private static class DeleteDiaryAsync extends AsyncTask<Diary, Void, Void>{
        DiaryDao mDiaryDao;

        public DeleteDiaryAsync(DiaryDao diaryDao) {
            mDiaryDao = diaryDao;
        }

        @Override
        protected Void doInBackground(Diary... diaries) {
            mDiaryDao.deleteDiary(diaries[0]);
            return null;
        }
    }
}
