package com.example.lovereminder;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lovereminder.databinding.FragmentMainBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements DialogFragment.Listener, View.OnClickListener, ChangeDateDialog.Listener, ChangeThemeDialog.ThemeDialogListener {

    interface SettingsListener{
        void onBackgroundImageChanged(Uri uri);
    }
    private int flag; // to distinguish you from your friend
    private TextView tv_yourName;
    private TextView tv_yourFrName;
    private TextView tv_Openning;
    private TextView tv_days;
    private CircleImageView civ_yourPicture;
    private CircleImageView civ_yourFrPicture;
    private ImageView iv_heart;
    private LinearLayout mainFrag;

    private SharedPreferences sharedPreferences;
    private FragmentMainBinding binding;

    int height;
    int width;
    private SettingsListener listener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        Log.d("Tag", "Main Frag Destroyed");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.d("Tag", "Main Frag Stopped");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d("Tag", "Main Frag Resumed");
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("Tag", "Main Frag created");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.d("Tag", "Main Frag Destroyed View");
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        listener = (SettingsListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        binding = FragmentMainBinding.bind(v);
        connectViews(binding);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getSharedPreferences("userInfor", Context.MODE_PRIVATE);

        String bundleStrYourName = getArguments().getString("yourName");
        String bundleStrYourFrName = getArguments().getString("yourFrName");
        String bundleStrDays = getArguments().getString("date");

        loadUserData(bundleStrYourName, bundleStrYourFrName, bundleStrDays);

        final Animation zoomin = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        iv_heart.startAnimation(zoomin);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_background:
                flag = 2;
                changePicture();
                return true;
            case R.id.action_change_date:
                showPopUpChangeDate();
                return true;
            case R.id.action_change_theme:
                showPopupChangeTheme();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupChangeTheme() {
        ChangeThemeDialog changeDateDialog = new ChangeThemeDialog();
        changeDateDialog.setTargetFragment(this, 123);
        changeDateDialog.show(getFragmentManager(), "ChangeThemeDialog");
    }

    private void loadUserData(String bundleStrYourName, String bundleStrYourFrName, String bundleStrDays) {
        if (sharedPreferences.getString("yourImg", "") != "")
            loadUserImg();

        try {
            setInfor(bundleStrYourName, bundleStrYourFrName, bundleStrDays);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void connectViews(FragmentMainBinding binding) {
        civ_yourPicture = binding.profileImage;
        civ_yourFrPicture = binding.friendProfileImage;
        tv_yourName = binding.tvYourName;
        iv_heart = binding.imgHeart;
        tv_yourFrName = binding.tvYourFrName;
        tv_days = binding.tvDayCount;
        mainFrag = binding.mainFragLinear;

        mainFrag.setOnClickListener(this);
        tv_yourFrName.setOnClickListener(this);
        tv_yourName.setOnClickListener(this);
        tv_days.setOnClickListener(this);
        civ_yourFrPicture.setOnClickListener(this);
        civ_yourPicture.setOnClickListener(this);
    }

    private void loadUserImg() {
        String yourImg = sharedPreferences.getString("yourImg", "");
        String yourFrImg = sharedPreferences.getString("yourFrImg", "");
        Uri yourUri = Uri.parse(yourImg);
        Uri yourFrUri = Uri.parse(yourFrImg);

        Glide.with(getActivity())
                .load(yourUri)
                .into(civ_yourPicture);
        Glide.with(getActivity())
                .load(yourFrUri)
                .into(civ_yourFrPicture);
    }

    private void setInfor(String yourName, String yourFrName, String Days) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String date_start_string = "";
        date_start_string = Days;

        tv_yourName.setText(yourName);
        tv_yourFrName.setText(yourFrName);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date_start = simpleDateFormat.parse(date_start_string);
        String date_end_string = simpleDateFormat.format(calendar.getTime());
        Date date_end = simpleDateFormat.parse(date_end_string);
        long diff = date_end.getTime() - date_start.getTime();
        tv_days.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
    }

    @Override
    public void ApplyChange(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (flag == 0) {
            tv_yourName.setText(username);
            editor.putString("yourName", username);
        } else {
            tv_yourFrName.setText(username);
            editor.putString("yourFrName", username);
        }
        editor.apply();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                changePicture();
                flag = 0;
                break;
            case R.id.friend_profile_image:
                changePicture();
                flag = 1;
                break;
            case R.id.tv_yourName:
                flag = 0;
                ShowPopUpChangeName();
                break;
            case R.id.tv_yourFrName:
                flag = 1;
                ShowPopUpChangeName();
                break;
            case R.id.main_frag_linear:
                showPopUpChangeDate();
        }
    }

    private void showPopUpChangeDate() {
        ChangeDateDialog changeDateDialog = new ChangeDateDialog();

        changeDateDialog.setTargetFragment(MainFragment.this, 2);
        changeDateDialog.show(getFragmentManager(), "custom");
    }


    private void ShowPopUpChangeName() {
        DialogFragment dialogFragment = new DialogFragment();
        Bundle bundle = new Bundle();
        if (flag == 0)
            bundle.putString("name", tv_yourName.getText().toString().trim());
        else bundle.putString("name", tv_yourFrName.getText().toString().trim());

        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(MainFragment.this, 1);
        dialogFragment.show(getFragmentManager(), "custom dialog");
    }

    private void changePicture() {
        int actionBarHeight = ((AppCompatActivity)getActivity()).getSupportActionBar().getHeight();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setAspectRatio(width ,height - actionBarHeight)
                .setFixAspectRatio(true)
                .setRequestedSize(width, height - actionBarHeight, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (flag == 0) {
                    civ_yourPicture.setImageURI(result.getUri());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("yourImg", result.getUri().toString());
                    editor.apply();
                } else if (flag == 1) {
                    civ_yourFrPicture.setImageURI(result.getUri());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("yourFrImg", result.getUri().toString());
                    editor.apply();
                } else if (flag == 2) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    try {
                        BitmapFactory.decodeStream(
                                getActivity().getContentResolver().openInputStream(result.getUri()),
                                null,
                                options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        Log.d("RESULT METRICS", "WIDTH: " + imageWidth);
                        Log.d("RESULT METRICS", "HEIGHT: " + imageHeight);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (listener != null) {
                        listener.onBackgroundImageChanged(result.getUri());
                    }
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void ApplyDateChange(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date_start = null;
        try {
            date_start = simpleDateFormat.parse(date);
            String date_end_string = simpleDateFormat.format(calendar.getTime());
            Date date_end = simpleDateFormat.parse(date_end_string);
            long diff = date_end.getTime() - date_start.getTime();
            tv_days.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Nhập sai định dạng", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", date);
        editor.apply();

        //change the alarm
        changeAlarm();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        Intent intent = new Intent(requireActivity(), CoupleDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                pendingIntent);
    }

    @Override
    public void onThemeDialogChanged(int themeId) {
        SharedPreferences.Editor editor = getActivity().
                getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                .edit();
        editor.putInt("theme_color", themeId).apply();
    }
}
