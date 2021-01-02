package com.example.lovereminder;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends Fragment {

    private ArrayList<Diary> diaries;
    private DiaryAdapter adapter;
    private ListView lvDiaries;
    private TextView tvNothing;

    SharedPreferences sharedPreferences;


    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onDestroy() {
        Log.d("Tag", "Dia Frag Destroyed");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.d("Tag", "Dia Frag Stopped");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d("Tag", "Dia Frag Resumed");
        checkSizeOfDiaryList();

        super.onResume();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        sharedPreferences = getActivity().getSharedPreferences("lst_diary", Context.MODE_PRIVATE);

        loadDiaries();
        adapter = new DiaryAdapter(getActivity() ,1 , diaries);
        lvDiaries.setAdapter(adapter);

        return v;
    }

    private void checkSizeOfDiaryList() {
        if (diaries.size() == 0) {
            tvNothing.setVisibility(View.VISIBLE);
            lvDiaries.setVisibility(View.INVISIBLE);
        } else {
            tvNothing.setVisibility(View.INVISIBLE);
            lvDiaries.setVisibility(View.VISIBLE);
        }
    }

    private void connectViews(View v) {
        lvDiaries = (ListView) v.findViewById(R.id.lv_diaries);
        tvNothing = (TextView) v.findViewById(R.id.tv_nothing);

        lvDiaries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DiaryActivity.class);
                intent.putExtra("date", diaries.get(position).getDate());
                intent.putExtra("content", diaries.get(position).getContent());
                intent.putExtra("lst_position", position);
                startActivity(intent);
                getActivity().finish();
            }
        });

        lvDiaries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.remove_item_listview);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_remove_item:
                                diaries.remove(position);
                                adapter.notifyDataSetChanged();

                                Gson gson = new Gson();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String json = gson.toJson(diaries);
                                editor.putString("lst_diary", json);
                                editor.apply();

                                checkSizeOfDiaryList();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    private void loadDiaries() {

        if(sharedPreferences.getString("lst_diary", null)!= null){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("lst_diary", null);
            Type type =  new TypeToken<ArrayList<Diary>>(){}.getType();
            diaries = gson.fromJson(json, type);
            Collections.reverse(diaries);
        }
        else diaries = new ArrayList<Diary>();

    }

}
