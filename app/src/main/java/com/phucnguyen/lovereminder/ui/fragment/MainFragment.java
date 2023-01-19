package com.phucnguyen.lovereminder.ui.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeDateDialog;
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeThemeDialog;
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver;
import com.phucnguyen.lovereminder.R;
import com.phucnguyen.lovereminder.databinding.FragmentMainBinding;
import com.phucnguyen.lovereminder.ui.activity.BaseActivity;
import com.phucnguyen.lovereminder.ui.fragment.dialog.DialogFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements DialogFragment.Listener, View.OnClickListener, ChangeDateDialog.Listener, ChangeThemeDialog.ThemeDialogListener {

    public static final String DEFAULT_NAME = "Không xác định";
    public static final String DEFAULT_COUPLE_DATE = "26/12/1965";

    public interface SettingsListener{
        void onBackgroundImageChanged(Uri uri);
    }
    private static final String TAG = MainFragment.class.getSimpleName();
    private int flag; // to distinguish you from your friend

    private SharedPreferences sharedPreferences;
    private FragmentMainBinding binding;

    int height;
    int width;
    private SettingsListener listener;

    private String ownerName;
    private String partnerName;
    private String coupleDate;

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

        ownerName = sharedPreferences.getString("yourName", DEFAULT_NAME);
        partnerName = sharedPreferences.getString("yourFrName", DEFAULT_NAME);
        coupleDate = sharedPreferences.getString("date", DEFAULT_COUPLE_DATE);

        loadUserData();

        final Animation zoomin = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        binding.imgHeart.startAnimation(zoomin);

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

    private void loadUserData() {
        if (sharedPreferences.getString("yourImg", "") != "")
            loadUserImg();

        try {
            setInfor(ownerName, partnerName, coupleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void connectViews(FragmentMainBinding binding) {
        binding.mainFragLinear.setOnClickListener(this);
        binding.tvYourFrName.setOnClickListener(this);
        binding.tvYourName.setOnClickListener(this);
        binding.tvDayCount.setOnClickListener(this);
        binding.friendProfileImage.setOnClickListener(this);
        binding.profileImage.setOnClickListener(this);
    }

    private void loadUserImg() {
        String yourImg = sharedPreferences.getString("yourImg", "");
        String yourFrImg = sharedPreferences.getString("yourFrImg", "");
        Uri yourUri = Uri.parse(yourImg);
        Uri yourFrUri = Uri.parse(yourFrImg);

        Glide.with(getActivity())
                .load(yourUri)
                .into(binding.profileImage);
        Glide.with(getActivity())
                .load(yourFrUri)
                .into(binding.friendProfileImage);
    }

    private void setInfor(String yourName, String yourFrName, String Days) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String date_start_string = "";
        date_start_string = Days;

        binding.tvYourName.setText(yourName);
        binding.tvYourFrName.setText(yourFrName);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date_start = simpleDateFormat.parse(date_start_string);
        String date_end_string = simpleDateFormat.format(calendar.getTime());
        Date date_end = simpleDateFormat.parse(date_end_string);
        long diff = date_end.getTime() - date_start.getTime();
        binding.tvDayCount.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
    }

    @Override
    public void ApplyChange(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (flag == 0) {
            binding.tvYourName.setText(username);
            editor.putString("yourName", username);
        } else {
            binding.tvYourFrName.setText(username);
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
            bundle.putString("name", binding.tvYourName.getText().toString().trim());
        else bundle.putString("name", binding.tvYourFrName.getText().toString().trim());

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
                    binding.profileImage.setImageURI(result.getUri());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("yourImg", result.getUri().toString());
                    editor.apply();
                } else if (flag == 1) {
                    binding.friendProfileImage.setImageURI(result.getUri());
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
            binding.tvDayCount.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
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
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.i(TAG, String.format("Couple data has been changed. Next alarm at: %d", calendar.getTimeInMillis()));

        Intent intent = new Intent(requireActivity(), CoupleDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);
    }

    @Override
    public void onThemeDialogChanged(int themeId) {
        int newThemeId = 0;
        switch (themeId) {
            case R.color.colorPrimary:
                newThemeId = R.style.AppTheme;
                break;
            case R.color.blue:
                newThemeId = R.style.AppTheme_Blue;
                break;
        }
        SharedPreferences.Editor editor = getActivity().
                getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                .edit();
        editor.putInt("theme_color", themeId).apply();
        ((BaseActivity)(getActivity())).switchTheme(newThemeId);
    }
}
