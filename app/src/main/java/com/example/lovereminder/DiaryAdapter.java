package com.example.lovereminder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DiaryAdapter extends ArrayAdapter<Diary> {


    private Activity context;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.diary_item, null,
                    false);
        }
        Diary diary = getItem(position);
        TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);

        tv_date.setText(diary.getDate());
        tv_content.setText(diary.getContent());
        return convertView;
    }

    public DiaryAdapter(@NonNull Activity context, int resource, @NonNull List<Diary> objects) {
        super(context, resource, objects);
        this.context = context;
    }
}
