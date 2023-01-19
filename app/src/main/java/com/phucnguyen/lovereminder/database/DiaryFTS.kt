package com.phucnguyen.lovereminder.database

import androidx.room.Entity
import androidx.room.Fts4
import com.phucnguyen.lovereminder.model.Diary

@Entity(tableName = "diaries_fts")
@Fts4(contentEntity = Diary::class)
class DiaryFTS {
    var content: String? = null
}