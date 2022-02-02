package com.phucnguyen.lovereminder;


import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.phucnguyen.lovereminder.databinding.FragmentPictureBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment implements View.OnClickListener {

    private static final int RESULT_OK = -1;
    public static final int READ_EXTERNAL_STORAGE_REQ = 100;
    public static final int REQUEST_CHOOSE_IMAGE = 443;
    public static final String LOG_TAG = "PictureFragment";
    public static final String PICTURES_FOLDER_NAME = "saved-pictures";
    private GridView gvPictures;
    private ImageAdapter adapter;
    private LinearLayout linearLayout;
    private FloatingActionButton fabAddImage;

    private List<Image> images = new ArrayList<Image>();
    private FragmentPictureBinding binding;
    private PictureViewModel pictureViewModel;
    private int numOfPicturesTobeDeleted;

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //workaround for saving adapter's state
        adapter = new ImageAdapter(requireActivity(), images);
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

        pictureViewModel = new ViewModelProvider(requireActivity()).get(PictureViewModel.class);

        gvPictures = binding.gvPictures;
        linearLayout = binding.linearLayout;
        fabAddImage = binding.fabAddImage;

        gvPictures.setAdapter(adapter);

        pictureViewModel.getImages().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                PictureFragment.this.images = images;
                if (images.size() == 0) {
                    linearLayout.setVisibility(View.VISIBLE);
                    gvPictures.setVisibility(View.GONE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                    gvPictures.setVisibility(View.VISIBLE);

                    adapter.setImages(images);
                }
            }
        });

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (numOfPicturesTobeDeleted == 0) {
                    Intent intent = new Intent(getActivity(), FullScreenPicActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", images.get(position).getUri());
                    startActivity(intent);
                } else {
                    if (adapter.toggleImagePositionToDelete(position))
                        numOfPicturesTobeDeleted++;
                    else {
                        numOfPicturesTobeDeleted--;
                        //if numOfPicturesTobeDeleted ==0 -> hide menu action
                        if (numOfPicturesTobeDeleted == 0)
                            requireActivity().invalidateOptionsMenu();
                    }
                }
            }
        };
        gvPictures.setOnItemClickListener(onItemClickListener);
        gvPictures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (numOfPicturesTobeDeleted == 0) {
                    numOfPicturesTobeDeleted++;
                    adapter.toggleImagePositionToDelete(position);
                    requireActivity().invalidateOptionsMenu();
                } else {
                    //toggle image selection for each long click event
                    if (adapter.toggleImagePositionToDelete(position))
                        numOfPicturesTobeDeleted++;
                    else {
                        numOfPicturesTobeDeleted--;
                        //if numOfPicturesTobeDeleted ==0 -> hide menu action
                        if (numOfPicturesTobeDeleted == 0) {
                            requireActivity().invalidateOptionsMenu();
                        }
                    }
                }
                return true;
            }
        });
        fabAddImage.setOnClickListener(this);
        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (numOfPicturesTobeDeleted > 0) {
            outState.putInt("num_of_pictures_to_be_deleted", numOfPicturesTobeDeleted);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_remove, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem deleteMenuItem = menu.findItem(R.id.action_delete_picture);
        MenuItem cancelMenuItem = menu.findItem(R.id.action_cancel);
        super.onPrepareOptionsMenu(menu);
        if (numOfPicturesTobeDeleted == 0) {
            deleteMenuItem.setVisible(false);
            cancelMenuItem.setVisible(false);
        } else {
            deleteMenuItem.setVisible(true);
            cancelMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_picture:
                showPopupConfirmDeletion();
                return true;
            case R.id.action_cancel:
                numOfPicturesTobeDeleted = 0;
                getActivity().invalidateOptionsMenu();
                //remove selection's effect on selected items
                adapter.resetTracker();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupConfirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format("Bạn muốn xóa %d ảnh đã chọn?", numOfPicturesTobeDeleted))
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //delete the selected pictures
                        removePic();
                        //reset deletion-related things and update menu actions
                        numOfPicturesTobeDeleted = 0;
                        adapter.resetTracker();
                        getActivity().invalidateOptionsMenu();
                    }

                    private void removePic() {
                        SparseBooleanArray indexesOfImagesToDelete = adapter.getImagesToDelete();
                        File pictureFolder = new File(getContext().getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES), PICTURES_FOLDER_NAME);
                        for (int i = indexesOfImagesToDelete.size() - 1; i >= 0; i--) {
                            //To handle ArrayIndexOutOfBound, we delete the image with higher index first
                            int indexInImageList = indexesOfImagesToDelete.keyAt(i);
                            String filePathName = images.get(indexInImageList).getUri().getLastPathSegment();
                            images.remove(indexInImageList);
                            //delete file in external dir
                            File fileToBeDeleted = new File(pictureFolder, filePathName);
                            fileToBeDeleted.delete();
                        }
                        Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                        pictureViewModel.setImages(images);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            //when user selects multiple images, data.getClipData will not null
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    //TODO: load image's thumbnail, not the whole image
                    Uri uri = clipData.getItemAt(i).getUri();
                    Image image = new Image();
                    image.setUri(uri);
                    images.add(0, image);

                    saveImageToExternalDir(uri);
                }
            } else if (data.getData() != null) {
                //TODO: load image's thumbnail, not the whole image
                Uri uri = data.getData();
                Image image = new Image();
                image.setUri(uri);
                images.add(0, image);

                saveImageToExternalDir(uri);
            }
            pictureViewModel.setImages(images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveImageToExternalDir(Uri uri) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Boolean> emitter) {
                try {
                    //saved the selected picture to external app-specific files
                    Calendar calendar = Calendar.getInstance();
                    long createdTime = calendar.getTimeInMillis(); //make each file unique
                    File file = new File(getContext().getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES), PICTURES_FOLDER_NAME);
                    if (!file.exists() && !file.mkdir()) {
                        Log.e(LOG_TAG, "Directory not created");
                        emitter.onError(new Throwable("ERROR"));
                    } else {
                        File imageFileToSave = new File(file, createdTime + ".png");
                        try {
                            if (imageFileToSave.createNewFile()) {
                                OutputStream outputStream = new FileOutputStream(imageFileToSave);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.close();
                                emitter.onComplete();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> Log.d(LOG_TAG, result.toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddImage:
                if (numOfPicturesTobeDeleted == 0)
                    chooseImagesFromGallery();
                else
                    Toast.makeText(requireContext(), "Vui lòng hoàn thành thao tác khác trước", Toast.LENGTH_SHORT).show();
                break;
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
