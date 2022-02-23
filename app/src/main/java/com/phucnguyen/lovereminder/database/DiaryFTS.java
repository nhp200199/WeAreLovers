package com.phucnguyen.lovereminder.database;

import androidx.room.Entity;
import androidx.room.Fts4;

import com.phucnguyen.lovereminder.model.Diary;

@Entity(tableName = "diaries_fts")
@Fts4(contentEntity = Diary.class)
public class DiaryFTS {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
