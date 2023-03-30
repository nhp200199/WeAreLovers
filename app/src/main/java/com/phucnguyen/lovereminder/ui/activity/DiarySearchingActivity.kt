package com.phucnguyen.lovereminder.ui.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.ui.adapter.DiaryAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DiarySearchingActivity : BaseActivity() {
    private var svDiary: SearchView? = null
    private var rvDiaries: RecyclerView? = null
    private val diaries: List<Diary>? = null
    private var mQuery: String? = null
    private var mAdapter: DiaryAdapter? = null
    private var mDiaryDao: DiaryDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_searching)
        mDiaryDao = getInstance(this).diaryDao
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        rvDiaries = findViewById<View>(R.id.rcv_diaries) as RecyclerView
        mAdapter = DiaryAdapter(this)
        rvDiaries!!.adapter = mAdapter
        rvDiaries!!.layoutManager = LinearLayoutManager(this)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            mQuery = intent.getStringExtra(SearchManager.QUERY)
            Toast.makeText(this, mQuery, Toast.LENGTH_SHORT).show()
            //use the query to search data
            mDiaryDao!!.getDiariesBasedOnString("*$mQuery*")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { searchedDiaries: List<Diary?> ->
                    mAdapter!!.submitList(searchedDiaries)
                    mAdapter!!.setListener(object : DiaryAdapter.Listener {
                        override fun onDiaryLongClickListener(diary: Diary, v: View) {}

                        override fun onDiaryClickListener(diary: Diary) {
                            val intent =
                                Intent(this@DiarySearchingActivity, DiaryActivity::class.java)
                            intent.putExtra("id", diary?.id)
                            startActivity(intent)
                        }
                    })
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_diary, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.action_search_diary)
        svDiary = searchMenuItem.actionView as SearchView?
        svDiary!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
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
        //automatically expand the action view
        searchMenuItem.expandActionView()
        svDiary!!.setQuery(mQuery, false)
        return super.onCreateOptionsMenu(menu)
    }
}