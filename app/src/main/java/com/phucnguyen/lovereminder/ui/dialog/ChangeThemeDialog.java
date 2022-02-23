package com.phucnguyen.lovereminder.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phucnguyen.lovereminder.R;
import com.phucnguyen.lovereminder.ui.adapter.ThemesAdapter;

public class ChangeThemeDialog extends AppCompatDialogFragment {
    public interface ThemeDialogListener {
        void onThemeDialogChanged(int themeId);
    }
    private RecyclerView rcvThemes;
    private ThemesAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_theme, null);
        connectView(view);

        builder.setView(view)
                .setTitle("Thay đổi theme")
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        int themeId = adapter.getCurrentSelectedThemeId();
                        ((ThemeDialogListener) getTargetFragment()).onThemeDialogChanged(themeId);
                    }
                });

        return builder.create();
    }

    private void connectView(View view) {
        rcvThemes = view.findViewById(R.id.rcvThemes);

        rcvThemes.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new ThemesAdapter(view.getContext());
        rcvThemes.setAdapter(adapter);
    }
}
