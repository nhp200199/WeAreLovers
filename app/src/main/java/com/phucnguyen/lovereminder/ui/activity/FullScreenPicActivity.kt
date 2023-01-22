package com.phucnguyen.lovereminder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.phucnguyen.lovereminder.R;

import java.util.ArrayList;
import java.util.Arrays;

public class FullScreenPicActivity extends BaseActivity {
    private PhotoView iv_FullPic;

    private SharedPreferences sharedPreferences;
    private ArrayList<String> arrlst_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_full_screen_pic);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("picture", MODE_PRIVATE);

        String lst_picture = sharedPreferences.getString("lst_picture", null);
        arrlst_pic = new ArrayList<String>(Arrays.asList((lst_picture.split(","))));
        for(int i = 0; i< arrlst_pic.size();i++){
            Log.d("TAG", "[" + i +"]" + ": " + arrlst_pic.get(i));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.photo_view);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, arrlst_pic);
        viewPager.setAdapter(adapter);

        //reverse the array
        int i = arrlst_pic.size() - getIntent().getIntExtra("position", -1);
        viewPager.setCurrentItem(i);
        viewPager.setRotationY(180);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_remove_item:
                showConfirmPopUp();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    private void showConfirmPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn muốn xóa ảnh này")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        removePic();
                    }

                    private void removePic() {
                        String lst_picture = sharedPreferences.getString("lst_picture", null);
                        ArrayList<String> arrlst_pic;
                        arrlst_pic = new ArrayList<String>(Arrays.asList((lst_picture.split(","))));
                        arrlst_pic.remove(arrlst_pic.size()- getIntent().getIntExtra("position", -1));

                        StringBuilder builder1 = new StringBuilder();
                        for(int i =0; i < arrlst_pic.size(); i++){
                            if(i==0){
                                builder1.append(arrlst_pic.get(i));
                            }
                            else {
                                builder1.append(",");
                                builder1.append(arrlst_pic.get(i));
                            }
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lst_picture", builder1.toString());
                        editor.apply();

                        onBackPressed();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.remove_item_listview, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private class ViewPagerAdapter extends PagerAdapter {
        private Context context;
        private ArrayList<String> imageUrls;

        ViewPagerAdapter(Context context, ArrayList<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PhotoView imageView = new PhotoView(context);
            Glide.with(FullScreenPicActivity.this)
                    .load(imageUrls.get(position))
                    .into(imageView);
            container.addView(imageView);
            Log.d("TAG", String.valueOf(getIntent().getIntExtra("position", -1)));
            imageView.setRotationY(180);


            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
