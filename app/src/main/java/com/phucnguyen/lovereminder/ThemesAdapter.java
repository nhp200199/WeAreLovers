package com.phucnguyen.lovereminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ThemeViewHolder> {
    private Context mContext;
    private List<Integer> colorIntsList = Arrays.asList(
            R.color.colorPrimary,
            R.color.blue
    );
    private SharedPreferences mSharedPreferences;
    private int currentSelectedThemeIdPosition;

    public ThemesAdapter(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int selectedThemeColorId = mSharedPreferences.getInt("theme_color", 0);
        currentSelectedThemeIdPosition = colorIntsList.indexOf(selectedThemeColorId);
    }

    @NotNull
    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_theme_color, parent, false);
        return new ThemeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ThemeViewHolder holder, int position) {
        holder.civTheme.setImageResource(colorIntsList.get(position));
        if (position == currentSelectedThemeIdPosition)
            holder.civThemeChecked.setVisibility(View.VISIBLE);
        else holder.civThemeChecked.setVisibility(View.GONE);

        holder.civTheme.setOnClickListener(v -> changeSelectedThemePosition(position));
    }

    private void changeSelectedThemePosition(int newlySelectedThemeIdPosition) {
        notifyItemChanged(currentSelectedThemeIdPosition); //unselect the old one
        currentSelectedThemeIdPosition = newlySelectedThemeIdPosition;
        notifyItemChanged(currentSelectedThemeIdPosition);
    }

    @Override
    public int getItemCount() {
        return colorIntsList.size();
    }

    public int getCurrentSelectedThemeId() {
        return colorIntsList.get(currentSelectedThemeIdPosition);
    }

    public class ThemeViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civTheme;
        private CircleImageView civThemeChecked;

        public ThemeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            civTheme = itemView.findViewById(R.id.civThemeCorlor);
            civThemeChecked = itemView.findViewById(R.id.civThemeChecked);
        }
    }
}
