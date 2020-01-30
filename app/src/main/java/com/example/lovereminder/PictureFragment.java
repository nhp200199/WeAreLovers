package com.example.lovereminder;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment implements View.OnClickListener{

    private static final int RESULT_OK = -1;
    private ArrayList<Uri> images;
    private GridView gvPictures;
    private ImageAdapter adapter;
    private LinearLayout linearLayout;
    private CircleImageView civ_insertPicture;

    private SharedPreferences sharedPreferences;
    private StringBuilder builder_lst_pictures;
    private boolean checkPictrue;

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        Log.d("Tag", "Pic Frag Destroyed");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.d("Tag", "Pic Frag Stopped");
        super.onStop();
    }

    @Override
    public void onResume() {
        loadPictures();

        if(builder_lst_pictures.length() !=0)
        {
            linearLayout.setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("Tag", "Pic Frag created");

        super.onCreate(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        Log.d("Tag", "Pic Frag Destroyed View");
        super.onDestroyView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_picture, container, false);
        gvPictures = v.findViewById(R.id.gv_Pictures);
        linearLayout = v.findViewById(R.id.linear_layout);
        civ_insertPicture = v.findViewById(R.id.civ_insertPictures);

        sharedPreferences = getActivity().getSharedPreferences("picture", Context.MODE_PRIVATE);

        /*
        if(images.size() == 0){
            linearLayout.setVisibility(View.VISIBLE);
            gvPictures.setVisibility(View.GONE);
        }
        else{
            linearLayout.setVisibility(View.GONE);
            gvPictures.setVisibility(View.VISIBLE);
        }*/


        gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), 443);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", images.get(position));
                    startActivity(intent);
                }
            }
        });

        civ_insertPicture.setOnClickListener(this);

        return v;
    }

    private void loadPictures() {
        if(sharedPreferences.getString("lst_picture", null)!= null){
            images = new ArrayList<Uri>();
            adapter = new ImageAdapter(getActivity(), images);
            gvPictures.setAdapter(adapter);
            String lst_picture = sharedPreferences.getString("lst_picture", null);
            ArrayList<String> arrlst_pic;

            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.couple) + '/' + getResources().getResourceTypeName(R.drawable.couple) + '/' + String.valueOf(R.drawable.couple) );
            images.add(0,imageUri);

            arrlst_pic = new ArrayList<String>(Arrays.asList((lst_picture.split(","))));
            for (String i: arrlst_pic){
                images.add(1,Uri.parse(i));
            }
            adapter.notifyDataSetChanged();

            builder_lst_pictures = new StringBuilder();
            for(int i = images.size()-1; i >= 1; i--){
                if(i==images.size()-1){
                    builder_lst_pictures.append(images.get(i));
                }
                else {
                    builder_lst_pictures.append(",");
                    builder_lst_pictures.append(images.get(i));
                }
            }


        }
        else {
            builder_lst_pictures = new StringBuilder();
            images = new ArrayList<Uri>();
            adapter = new ImageAdapter(getActivity(), images);
            gvPictures.setAdapter(adapter);
        }
        Toast.makeText(getActivity(), "so luong anh " + images.size(), Toast.LENGTH_LONG).show();
        for(int i = 0; i< images.size();i++){
            Log.d("TAG2", "[" + i +"]" + ": " + images.get(i));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 443 && resultCode == RESULT_OK && data.getData() != null){
            linearLayout.setVisibility(View.INVISIBLE);
            Uri uri = data.getData();
            images.add(uri);
            saveImage(uri.toString());

        }

        else if(gvPictures.getChildCount() != 0){
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.couple) + '/' + getResources().getResourceTypeName(R.drawable.couple) + '/' + String.valueOf(R.drawable.couple) );
            images.add(imageUri);
            adapter.notifyDataSetChanged();
        }
        else {
            gvPictures.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveImage(String uri) {
        if(builder_lst_pictures.length() == 0)
        {
            builder_lst_pictures.append(uri);
        }
        else {
            builder_lst_pictures.append(",");
            builder_lst_pictures.append(uri);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lst_picture", builder_lst_pictures.toString());
        editor.apply();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civ_insertPictures:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), 443);
        }
    }
}
