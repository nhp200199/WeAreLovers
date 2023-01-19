package com.phucnguyen.lovereminder.ui.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.phucnguyen.lovereminder.R;
import com.phucnguyen.lovereminder.databinding.ChangeDateBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeDateDialog extends AppCompatDialogFragment {
    private EditText editTextDate;
    private ChangeDateDialog.Listener listener;
    private ChangeDateBinding binding;
    private static final String TAG = "MyCustomDialog";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (ChangeDateDialog.Listener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_date, null);
        binding = ChangeDateBinding.bind(view);
        editTextDate = binding.edtDate;

        builder.setView(view)
                .setTitle("Sửa ngày tháng bắt đầu")
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String date = editTextDate.getText().toString();
                        Date date_start = null;
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            date_start = simpleDateFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Nhập sai định dạng", Toast.LENGTH_SHORT).show();
                        }
                        if(date_start!=null)
                        listener.ApplyDateChange(date);
                    }
                });

        return builder.create();
    }
    public interface Listener{
        void ApplyDateChange(String date);
    }
}
