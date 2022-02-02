package com.phucnguyen.lovereminder;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diaries")
public class Diary {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long date;
    @NonNull
    private String content;

    public Diary(){

    }
    public Diary(long date, String content){
        this.date = date;
        this.content = content;
    }
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
