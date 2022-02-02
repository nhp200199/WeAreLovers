package com.phucnguyen.lovereminder;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    Context context;
    List<Image> images;
    private SparseBooleanArray imagesToDeleteTracker;

    @Override
    public int getCount() {
        return images.size();
    }

    public ImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
        this.imagesToDeleteTracker = new SparseBooleanArray();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        if (imagesToDeleteTracker.get(position)) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(330, 350));
            imageView.setCropToPadding(true);

            Glide.with(context)
                    .load(images.get(position).getUri())
                    .thumbnail(0.5f)
                    .into(imageView);

            imageView.setBackgroundResource(R.drawable.view_border);
        } else {
            Glide.with(context)
                    .load(images.get(position).getUri())
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(330, 350));
        }
        return imageView;

    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {

        return true;
    }

    public void setImages(List<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public boolean toggleImagePositionToDelete(int position) {
        boolean result;
        if (imagesToDeleteTracker.get(position)) {
            result = false;
            imagesToDeleteTracker.delete(position);
        } else {
            result = true;
            imagesToDeleteTracker.put(position, true);
        }
        notifyDataSetChanged();
        return result;
    }

    public SparseBooleanArray getImagesToDelete() {
        return imagesToDeleteTracker;
    }

    public void resetTracker() {
        imagesToDeleteTracker.clear();
    }
}
