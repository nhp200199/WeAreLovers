package com.example.lovereminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DiaryAdapter extends ListAdapter<Diary, DiaryAdapter.DiaryViewHolder> {
    interface Listener{
        void onDiaryLongClickListener(Diary diary, View v);
        void onDiaryClickListener(Diary diary);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private Listener mListener;
    private Context mContext;

    protected DiaryAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }


    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.diary_item, parent,
                false);
        return new DiaryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DiaryViewHolder holder, int position) {
        final Diary diary = getItem(position);
        //transform the date retrieved from database
        long dateToBeFormat = diary.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date dateObject = new Date(dateToBeFormat);
        String formattedDateString = simpleDateFormat.format(dateObject);
//        Date parsedDate = null;
//        try {
//            parsedDate = simpleDateFormat.parse(dateToBeFormat);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(parsedDate);
//        String formattedString = String.format("ngày %d tháng %d năm %d",
//                calendar.get(Calendar.DAY_OF_MONTH),
//                calendar.get(Calendar.MONTH) + 1,
//                calendar.get(Calendar.YEAR));

        holder.tv_date.setText(formattedDateString);
        holder.tv_content.setText(diary.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onDiaryClickListener(diary);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null)
                    mListener.onDiaryLongClickListener(diary, v);
                return true;
            }
        });
    }

     class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date, tv_content;
        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
    private static DiffUtil.ItemCallback<Diary> DIFF_CALLBACK = new DiffUtil.ItemCallback<Diary>() {
        @Override
        public boolean areItemsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
            return oldItem.getContent().equals(newItem.getContent());
        }
    };
}
