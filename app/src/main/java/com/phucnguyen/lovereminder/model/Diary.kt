package com.phucnguyen.lovereminder.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaries")
data class Diary @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = 0L,
    var content: String = ""
)