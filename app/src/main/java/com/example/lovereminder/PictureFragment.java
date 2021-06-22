package com.example.lovereminder;


import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovereminder.databinding.FragmentPictureBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment implements View.OnClickListener{

    private static final int RESULT_OK = -1;
    public static final int READ_EXTERNAL_STORAGE_REQ = 100;
    public static final int REQUEST_CHOOSE_IMAGE = 443;
    private GridView gvPictures;
    private ImageAdapter adapter;
    private LinearLayout linearLayout;
    private FloatingActionButton fabAddImage;

    private ArrayList<Image> images = new ArrayList<Image>();
    private FragmentPictureBinding binding;

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        Log.d("Tag", "Pic Frag Destroyed View");
        super.onDestroyView();
        binding = null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_picture, container, false);
        binding = FragmentPictureBinding.bind(v);

        gvPictures = binding.gvPictures;
        linearLayout = binding.linearLayout;
        fabAddImage = binding.fabAddImage;

        adapter = new ImageAdapter(requireActivity(), images);

        gvPictures.setAdapter(adapter);
        gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("uri", images.get(position).getUri());
                startActivity(intent);
            }
        });
        fabAddImage.setOnClickListener(this);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_remove, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_dairy:
                return false;
            case R.id.action_delete_picture:
                return  true;
            case R.id.action_cancel:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void loadPictures() {
//        arr_ItemSelectedIdex = new ArrayList<Integer>();
//        if(sharedPreferences.getString("lst_picture", null)!= null){
//            images = new ArrayList<Image>();
//            adapter = new ImageAdapter(getActivity(), images);
//            gvPictures.setAdapter(adapter);
//            String lst_picture = sharedPreferences.getString("lst_picture", null);
//
//            if(!lst_picture.equals("")){
//                ArrayList<String> arrlst_pic;
//
//                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources()
//                        .getResourcePackageName(R.drawable.couple) + '/' + getResources().getResourceTypeName(R.drawable.couple) + '/' + String.valueOf(R.drawable.couple) );
//                images.add(0,new Image());
//                images.get(0).setUri(imageUri);
//
//                arrlst_pic = new ArrayList<String>(Arrays.asList((lst_picture.split(","))));
//                //if(arrlst_pic.size())
//                for (String i: arrlst_pic){
//                    Image image = new Image();
//                    image.setUri(Uri.parse(i));
//                    images.add(1,image);
//                }
//                adapter.notifyDataSetChanged();
//                if(images.size() ==1)
//                {
//                    images.clear();
//                    linearLayout.setVisibility(View.VISIBLE);
//                    gvPictures.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    linearLayout.setVisibility(View.INVISIBLE);
//                    gvPictures.setVisibility(View.VISIBLE);
//                }
//
//                builder_lst_pictures = new StringBuilder();
//                for(int i = images.size()-1; i >= 1; i--){
//                    if(i==images.size()-1){
//                        builder_lst_pictures.append(images.get(i).getUri().toString());
//                    }
//                    else {
//                        builder_lst_pictures.append(",");
//                        builder_lst_pictures.append(images.get(i).getUri().toString());
//                    }
//                }
//            }
//
//        }
//        else {
//            builder_lst_pictures = new StringBuilder();
//            images = new ArrayList<Image>();
//            adapter = new ImageAdapter(getActivity(), images);
//            gvPictures.setAdapter(adapter);
//        }
//        Toast.makeText(getActivity(), "so luong anh " + images.size(), Toast.LENGTH_LONG).show();
//        for(int i = 0; i< images.size();i++){
//            Log.d("TAG2", "[" + i +"]" + ": " + images.get(i));
//        }
//        Log.d("Tag", "Reloaded");
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK){
            //when user selects multiple images, data.getClipData will not null
            if (data.getClipData() != null){
                ClipData clipData = data.getClipData();
                for(int i =0; i< clipData.getItemCount(); i++){
                    //TODO: load image's thumbnail, not the whole image
                    Uri uri = clipData.getItemAt(i).getUri();
                    Image image=  new Image();
                    image.setUri(uri);
                    images.add(image);
                }
            } else if (data.getData() != null){
                //TODO: load image's thumbnail, not the whole image
                Uri uri = data.getData();
                Image image=  new Image();
                image.setUri(uri);
                images.add(image);
            }
            adapter.notifyDataSetChanged();
        }
        else {
            gvPictures.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabAddImage:
                chooseImagesFromGallery();
        }
    }

    private void chooseImagesFromGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQ);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), REQUEST_CHOOSE_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQ && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                chooseImagesFromGallery();
            else
                Toast.makeText(requireContext(), "Bạn cần cho phép để truy cập ảnh", Toast.LENGTH_SHORT).show();
        }
    }
}
