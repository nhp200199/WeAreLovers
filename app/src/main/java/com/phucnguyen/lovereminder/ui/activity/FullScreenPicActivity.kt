package com.phucnguyen.lovereminder.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.model.Image
import com.phucnguyen.lovereminder.viewmodel.PictureDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FullScreenPicActivity : BaseActivity() {
    private val viewModel: PictureDetailViewModel by viewModels()
    private var picturePos = 0
    private val listener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            viewModel.currentImagePos = position
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_full_screen_pic)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(EXTRA_PICTURE_POS)) {
            picturePos = intent.getIntExtra(EXTRA_PICTURE_POS, 0)
        }

        viewPager = findViewById<View>(R.id.photo_view) as ViewPager
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.images.observe(this@FullScreenPicActivity) {
                    if (it.isNotEmpty()) {
                        adapter.images = it
                        viewPager.setCurrentItem(picturePos, false)
                    } else {
                        finish()
                    }
                }
            }
        }

        viewPager.addOnPageChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.removeOnPageChangeListener(listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove_item -> {
                showConfirmPopUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmPopUp() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn muốn xóa ảnh này")
            .setPositiveButton("Có") { dialog, which ->
                dialog.dismiss()
                viewModel.deleteCurrentImage()
            }
            .setNegativeButton("Không") { dialog, which -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.remove_item_listview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private inner class ViewPagerAdapter constructor(
        private val context: Context,
    ) : PagerAdapter() {
        var images: List<Image>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        override fun getCount(): Int {
            return images?.size ?: 0
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = PhotoView(context)
            Glide.with(this@FullScreenPicActivity)
                .load(images?.get(position)?.uri)
                .into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    companion object {
        const val EXTRA_PICTURE_POS = "EXTRA_PICTURE_POSITION"
    }
}