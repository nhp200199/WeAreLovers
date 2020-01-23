package com.example.lovereminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Arrays;

public class FullScreenPicActivity extends AppCompatActivity {
    private PhotoView iv_FullPic;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_pic);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("picture", MODE_PRIVATE);

        iv_FullPic = findViewById(R.id.photo_view);

        Uri image = (Uri)getIntent().getExtras().get("uri");

        Glide.with(this)
                .load(image)
                .into(iv_FullPic);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_remove_item:
                showConfirmPopUp();
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
                        arrlst_pic.remove(getIntent().getIntExtra("position", -1));

                        StringBuilder builder1 = new StringBuilder();
                        for (String i: arrlst_pic){
                            if(i.equals(arrlst_pic.get(0))){
                               builder1.append(i);
                            }
                            else {
                                builder1.append(",");
                                builder1.append(i);
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
}
