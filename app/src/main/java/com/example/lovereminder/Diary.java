package com.example.lovereminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diaries")
public class Diary {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String content;

    public Diary(){

    }
    public Diary(String date, String content){
        this.date = date;
        this.content = content;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
