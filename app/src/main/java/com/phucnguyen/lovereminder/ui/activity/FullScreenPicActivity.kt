package com.phucnguyen.lovereminder.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.phucnguyen.lovereminder.R
import java.util.*

class FullScreenPicActivity : BaseActivity() {
    private val iv_FullPic: PhotoView? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var arrlst_pic: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_full_screen_pic)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        sharedPreferences = getSharedPreferences("picture", MODE_PRIVATE)
        val lst_picture = sharedPreferences.getString("lst_picture", null)
        arrlst_pic = ArrayList(Arrays.asList(*lst_picture!!.split(",").toTypedArray()))
        for (i in arrlst_pic!!.indices) {
            Log.d("TAG", "[" + i + "]" + ": " + arrlst_pic!![i])
        }
        val viewPager = findViewById<View>(R.id.photo_view) as ViewPager
        val adapter = ViewPagerAdapter(this, arrlst_pic!!)
        viewPager.adapter = adapter

        //reverse the array
        val i = arrlst_pic!!.size - intent.getIntExtra("position", -1)
        viewPager.currentItem = i
        viewPager.rotationY = 180f
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
            .setPositiveButton("Có", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                    removePic()
                }

                private fun removePic() {
                    val lst_picture = sharedPreferences!!.getString("lst_picture", null)
                    val arrlst_pic: ArrayList<String>
                    arrlst_pic = ArrayList(Arrays.asList(*lst_picture!!.split(",").toTypedArray()))
                    arrlst_pic.removeAt(arrlst_pic.size - intent.getIntExtra("position", -1))
                    val builder1 = StringBuilder()
                    for (i in arrlst_pic.indices) {
                        if (i == 0) {
                            builder1.append(arrlst_pic[i])
                        } else {
                            builder1.append(",")
                            builder1.append(arrlst_pic[i])
                        }
                    }
                    val editor = sharedPreferences!!.edit()
                    editor.putString("lst_picture", builder1.toString())
                    editor.apply()
                    onBackPressed()
                }
            })
            .setNegativeButton("Không") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.remove_item_listview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private inner class ViewPagerAdapter internal constructor(
        private val context: Context,
        private val imageUrls: ArrayList<String>
    ) : PagerAdapter() {
        override fun getCount(): Int {
            return imageUrls.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = PhotoView(context)
            Glide.with(this@FullScreenPicActivity)
                .load(imageUrls[position])
                .into(imageView)
            container.addView(imageView)
            Log.d("TAG", intent.getIntExtra("position", -1).toString())
            imageView.rotationY = 180f
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}