package com.phucnguyen.lovereminder;

import android.net.Uri;

public class Image {
    private Uri uri;
    private String description;
    boolean isChecked = false;

    public Uri getUri() {
        return uri;
    }

    public Image(){};

    public Image(Uri uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean setCheck(boolean checked){
        return this.isChecked= checked;
    }
    public boolean isChecked(){
        return isChecked;
    }
    public void toggleChecked(){
        isChecked = !isChecked;
    }
}
