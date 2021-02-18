package com.soulmates.valley.data.local.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.soulmates.valley.data.local.room.base.BaseDao
import com.soulmates.valley.data.local.room.entity.SearchKeyword

@Dao
interface SearchKeywordDao :
    BaseDao<SearchKeyword> {
    @Query("SELECT * FROM SearchKeyword GROUP BY keywordName ORDER BY keywordId DESC LIMIT 20")
    fun getSearchKeyword(): LiveData<List<SearchKeyword>>

    @Query("DELETE FROM SearchKeyword")
    fun deleteAll()
}