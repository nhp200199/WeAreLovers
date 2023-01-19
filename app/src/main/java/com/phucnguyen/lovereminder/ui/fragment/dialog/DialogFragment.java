package com.phucnguyen.lovereminder.ui.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.phucnguyen.lovereminder.R;

public class DialogFragment extends AppCompatDialogFragment {
    private EditText editTextUsername;
    private Listener listener;
    private static final String TAG = "MyCustomDialog";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (Listener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Sửa Tên")
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = editTextUsername.getText().toString();
                        listener.ApplyChange(username);
                    }
                });

        String name = getArguments().getString("name");
        editTextUsername = view.findViewById(R.id.edt_userName);
        editTextUsername.setText(name);



        return builder.create();
    }
    public interface Listener{
        void ApplyChange(String username);
    }
}
