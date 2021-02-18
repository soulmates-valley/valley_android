package com.soulmates.valley.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.soulmates.valley.data.local.room.base.BaseDao
import com.soulmates.valley.data.local.room.entity.UserProfile
import io.reactivex.Single

@Dao
interface UserInfoDao :
    BaseDao<UserProfile> {
    @Query("SELECT * FROM UserInfo")
    fun getUserInfo(): Single<UserProfile>

    @Query("SELECT profileImg FROM UserInfo")
    fun getProfileImg(): String?

    @Query("DELETE FROM UserInfo")
    fun deleteAll()
}