package com.example.lovereminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiarySearchingActivity extends AppCompatActivity {

    private androidx.appcompat.widget.SearchView svDiary;
    private RecyclerView rvDiaries;

    private List<Diary> diaries;
    private String mQuery;
    private DiaryAdapter mAdapter;
    private DiaryDao mDiaryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_searching);

        mDiaryDao = AppDatabase.getInstance(this).getDiaryDao();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvDiaries = (RecyclerView) findViewById(R.id.rcv_diaries);
        mAdapter = new DiaryAdapter(this);
        rvDiaries.setAdapter(mAdapter);
        rvDiaries.setLayoutManager(new LinearLayoutManager(this));

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, mQuery, Toast.LENGTH_SHORT).show();
            //use the query to search data
            mDiaryDao.getDiariesBasedOnString("*" + mQuery + "*")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(searchedDiaries -> {
                        mAdapter.submitList(searchedDiaries);
                        mAdapter.setListener(new DiaryAdapter.Listener() {
                            @Override
                            public void onDiaryLongClickListener(Diary diary, View v) {

                            }

                            @Override
                            public void onDiaryClickListener(Diary diary) {
                                Intent intent = new Intent(DiarySearchingActivity.this, DiaryActivity.class);
                                intent.putExtra("id", diary.getId());
                                startActivity(intent);
                            }
                        });
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_diary, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search_diary);
        svDiary = (SearchView) searchMenuItem.getActionView();
        svDiary.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        svDiary.setMaxWidth(Integer.MAX_VALUE);
        //update the query in search view
        svDiary.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //remove search icon
        svDiary.setIconifiedByDefault(false);
        svDiary.setIconified(false); // this is important to hide the search hint icon
        ImageView icon = (ImageView) svDiary.findViewById(androidx.appcompat.R.id.search_mag_icon);
//        icon.setVisibility(View.GONE);
        icon.setImageDrawable(null);
        //automatically expand the action view
        searchMenuItem.expandActionView();
        svDiary.setQuery(mQuery, false);
        return super.onCreateOptionsMenu(menu);
    }
}