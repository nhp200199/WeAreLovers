package com.example.lovereminder;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment implements View.OnClickListener{

    private static final int RESULT_OK = -1;
    private ArrayList<Image> images;
    private GridView gvPictures;
    private ImageAdapter adapter;
    private LinearLayout linearLayout;
    private CircleImageView civ_insertPicture;
    private TextView showPicturesSum;

    private SharedPreferences sharedPreferences;
    private StringBuilder builder_lst_pictures;
    private ArrayList<Integer> arr_ItemSelectedIdex;
    int pic_count = 0;

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
        Log.d("Tag", "Pic Frag Resumed");
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
        showPicturesSum = getActivity().findViewById(R.id.tv_title);

        sharedPreferences = getActivity().getSharedPreferences("picture", Context.MODE_PRIVATE);

        gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    chooseImagesFromGallery();
                }
                else{
                    Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", images.get(position).getUri());
                    Toast.makeText(getActivity(), images.get(position).getUri().toString(), Toast.LENGTH_LONG).show();
                    startActivity(intent);

                    getActivity().finish();
                }
            }
        });

        gvPictures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setHasOptionsMenu(true);
                showPicturesSum.setText("Đã chọn " + ++pic_count + " ảnh");

                images.get(position).toggleChecked();
                adapter.notifyDataSetChanged();

                if(images.get(position).isChecked())
                {
                    arr_ItemSelectedIdex.add(position);
                }
                else{
                    arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                }


                gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        images.get(position).toggleChecked();
                        adapter.notifyDataSetChanged();

                        if(images.get(position).isChecked())
                        {
                            arr_ItemSelectedIdex.add(position);
                            showPicturesSum.setText("Đã chọn " + ++pic_count + " ảnh");
                        }
                        else{
                            arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                            showPicturesSum.setText("Đã chọn " + --pic_count+ " ảnh");
                        }

                    }
                });

                return true;
            }
        });

        civ_insertPicture.setOnClickListener(this);

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
            case R.id.action_create_dairy:
                return false;
            case R.id.action_delete_picture:
                showPopUpConfirm();
                return  true;
            case R.id.action_cancel:
                cancelChosenPictures();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cancelChosenPictures() {
        setHasOptionsMenu(false);
        showPicturesSum.setText("We Are Lovers");
        gvPictures.setOnItemClickListener(null);
        gvPictures.setOnItemLongClickListener(null);
        pic_count =0;

        gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    chooseImagesFromGallery();
                }
                else{
                    Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", images.get(position).getUri());
                    Toast.makeText(getActivity(), images.get(position).getUri().toString(), Toast.LENGTH_LONG).show();
                    startActivity(intent);

                    getActivity().finish();
                }
            }
        });

        gvPictures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setHasOptionsMenu(true);
                showPicturesSum.setText("Đã chọn " + ++pic_count + " ảnh");

                images.get(position).toggleChecked();
                adapter.notifyDataSetChanged();

                if(images.get(position).isChecked())
                {
                    arr_ItemSelectedIdex.add(position);
                    showPicturesSum.setText("Đã chọn " + ++pic_count + " ảnh");
                }
                else{
                    arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                    showPicturesSum.setText("Đã chọn " + --pic_count + " ảnh");
                }

                adapter.getView(position, view, parent).setEnabled(false);

                gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        images.get(position).toggleChecked();
                        adapter.notifyDataSetChanged();

                        if(images.get(position).isChecked())
                        {
                            arr_ItemSelectedIdex.add(position);
                        }
                        else{
                            arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                        }

                    }
                });

                return true;
            }
        });
        loadPictures();
        adapter.notifyDataSetChanged();
    }

    private void showPopUpConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn muốn xóa tất cả ảnh này?")
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

                        /* delete pictures in Grid View*/
                        boolean checked = false;
                        int y =0, z = 0;
                        Collections.sort(arr_ItemSelectedIdex);
                        for(Integer i: arr_ItemSelectedIdex){

                            if(checked == false){
                                images.remove(i.intValue());
                                checked =true;
                                y++;
                            }
                            else{
                                images.remove(i.intValue() - y);
                                y++;
                            }
                        }
                        if (images.size() == 1) {
                            gvPictures.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();

                        /* remove String in arrlst_pic*/

                        for(int i=0; i<arr_ItemSelectedIdex.size();i++){
                            if(i == 0){
                                arrlst_pic.remove(arrlst_pic.size() - arr_ItemSelectedIdex.get(i).intValue());
                                z++;
                            }
                            else{
                                arrlst_pic.remove(arrlst_pic.size() - arr_ItemSelectedIdex.get(i).intValue() +z);
                                z++;
                            }

                        }
                        arr_ItemSelectedIdex.clear();

                        /*save the remaining items*/

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


                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        cancelChosenPictures();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        /*set listeners on Grid View*/

        setHasOptionsMenu(false);
        showPicturesSum.setText("We Are Lovers");
        gvPictures.setOnItemClickListener(null);
        gvPictures.setOnItemLongClickListener(null);
        pic_count =0;

        gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    chooseImagesFromGallery();
                }
                else{
                    Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", images.get(position).getUri());
                    Toast.makeText(getActivity(), images.get(position).getUri().toString(), Toast.LENGTH_LONG).show();
                    startActivity(intent);

                    getActivity().finish();

                }
            }
        });

        gvPictures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setHasOptionsMenu(true);

                images.get(position).toggleChecked();
                adapter.notifyDataSetChanged();

                if(images.get(position).isChecked())
                {
                    arr_ItemSelectedIdex.add(position);
                    showPicturesSum.setText("Đã chọn " + ++pic_count + " ảnh");
                }
                else{
                    arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                    showPicturesSum.setText("Đã chọn " + --pic_count + " ảnh");
                }


                gvPictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        images.get(position).toggleChecked();
                        adapter.notifyDataSetChanged();

                        if(images.get(position).isChecked())
                        {
                            arr_ItemSelectedIdex.add(position);
                        }
                        else{
                            arr_ItemSelectedIdex.remove(arr_ItemSelectedIdex.indexOf(position));
                        }

                    }
                });

                return true;
            }
        });
    }


    private void loadPictures() {
        arr_ItemSelectedIdex = new ArrayList<Integer>();
        if(sharedPreferences.getString("lst_picture", null)!= null){
            images = new ArrayList<Image>();
            adapter = new ImageAdapter(getActivity(), images);
            gvPictures.setAdapter(adapter);
            String lst_picture = sharedPreferences.getString("lst_picture", null);

            if(!lst_picture.equals("")){
                ArrayList<String> arrlst_pic;

                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources()
                        .getResourcePackageName(R.drawable.couple) + '/' + getResources().getResourceTypeName(R.drawable.couple) + '/' + String.valueOf(R.drawable.couple) );
                images.add(0,new Image());
                images.get(0).setUri(imageUri);

                arrlst_pic = new ArrayList<String>(Arrays.asList((lst_picture.split(","))));
                //if(arrlst_pic.size())
                for (String i: arrlst_pic){
                    Image image = new Image();
                    image.setUri(Uri.parse(i));
                    images.add(1,image);
                }
                adapter.notifyDataSetChanged();
                if(images.size() ==1)
                {
                    images.clear();
                    linearLayout.setVisibility(View.VISIBLE);
                    gvPictures.setVisibility(View.INVISIBLE);
                }
                else {
                    linearLayout.setVisibility(View.INVISIBLE);
                    gvPictures.setVisibility(View.VISIBLE);
                }

                builder_lst_pictures = new StringBuilder();
                for(int i = images.size()-1; i >= 1; i--){
                    if(i==images.size()-1){
                        builder_lst_pictures.append(images.get(i).getUri().toString());
                    }
                    else {
                        builder_lst_pictures.append(",");
                        builder_lst_pictures.append(images.get(i).getUri().toString());
                    }
                }
            }

        }
        else {
            builder_lst_pictures = new StringBuilder();
            images = new ArrayList<Image>();
            adapter = new ImageAdapter(getActivity(), images);
            gvPictures.setAdapter(adapter);
        }
        Toast.makeText(getActivity(), "so luong anh " + images.size(), Toast.LENGTH_LONG).show();
        for(int i = 0; i< images.size();i++){
            Log.d("TAG2", "[" + i +"]" + ": " + images.get(i));
        }
        Log.d("Tag", "Reloaded");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 443 && resultCode == RESULT_OK && data.getClipData() != null){
            ClipData clipData = data.getClipData();

            for(int i =0; i< clipData.getItemCount(); i++){
                Uri uri = clipData.getItemAt(i).getUri();
                Image image=  new Image();
                image.setUri(uri);
                images.add(image);

                saveImage(uri.toString());
            }
            adapter.notifyDataSetChanged();


        }
        else if(data.getData()!=null){

            Uri uri = data.getData();
            Image image=  new Image();
            image.setUri(uri);
            images.add(image);
            saveImage(uri.toString());
            adapter.notifyDataSetChanged();
        }

        else if(gvPictures.getChildCount() != 0){
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.couple) + '/' + getResources().getResourceTypeName(R.drawable.couple) + '/' + String.valueOf(R.drawable.couple) );
            Image image=  new Image();
            image.setUri(imageUri);
            images.add(image);
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
                chooseImagesFromGallery();
        }
    }

    private void chooseImagesFromGallery() {
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);

            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), 443);
    }
}
