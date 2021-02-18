package com.soulmates.valley.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchKeyword")
data class SearchKeyword(
    @PrimaryKey(autoGenerate = true) val keywordId: Int = 0,
    @ColumnInfo(name = "keywordName") val keywordName: String
)