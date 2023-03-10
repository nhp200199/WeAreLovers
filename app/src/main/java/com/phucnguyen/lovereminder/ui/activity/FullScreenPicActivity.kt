package com.phucnguyen.lovereminder.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.launch

class FullScreenPicActivity : BaseActivity() {
    private lateinit var viewModel: PictureDetailViewModel
    private var picturePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_full_screen_pic)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(EXTRA_PICTURE_POS)) {
            picturePos = intent.getIntExtra(EXTRA_PICTURE_POS, 0)
        }

        val viewPager = findViewById<View>(R.id.photo_view) as ViewPager
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewModel = ViewModelProvider(this).get(PictureDetailViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.images.observe(this@FullScreenPicActivity) {
                    adapter.images = it
                    viewPager.setCurrentItem(picturePos, false)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove_item -> {
//                showConfirmPopUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    private fun showConfirmPopUp() {
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Bạn muốn xóa ảnh này")
//            .setPositiveButton("Có", object : DialogInterface.OnClickListener {
//                override fun onClick(dialog: DialogInterface, which: Int) {
//                    dialog.cancel()
//                    removePic()
//                }
//
//                private fun removePic() {
//                    val lst_picture = sharedPreferences!!.getString("lst_picture", null)
//                    val arrlst_pic: ArrayList<String>
//                    arrlst_pic = ArrayList(Arrays.asList(*lst_picture!!.split(",").toTypedArray()))
//                    arrlst_pic.removeAt(arrlst_pic.size - intent.getIntExtra("position", -1))
//                    val builder1 = StringBuilder()
//                    for (i in arrlst_pic.indices) {
//                        if (i == 0) {
//                            builder1.append(arrlst_pic[i])
//                        } else {
//                            builder1.append(",")
//                            builder1.append(arrlst_pic[i])
//                        }
//                    }
//                    val editor = sharedPreferences!!.edit()
//                    editor.putString("lst_picture", builder1.toString())
//                    editor.apply()
//                    onBackPressed()
//                }
//            })
//            .setNegativeButton("Không") { dialog, which -> dialog.cancel() }
//        val alertDialog = builder.create()
//        alertDialog.show()
//    }

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