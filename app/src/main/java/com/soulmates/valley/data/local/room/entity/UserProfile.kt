package com.soulmates.valley.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.soulmates.valley.data.local.room.converter.Converter
import com.soulmates.valley.data.model.user.UserInfo

@Entity(tableName = "UserInfo")
@TypeConverters(Converter::class)
data class UserProfile(
    @PrimaryKey(autoGenerate = false) val userId: Int,
    @ColumnInfo(name = "nickname") val nickname: String,
    @ColumnInfo(name = "profileImg") val profileImage: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "link") val link: String?,
    @ColumnInfo(name = "interest") val interest: List<Int>?
) {
    companion object {
        fun to(userInfo: UserInfo): UserProfile {
            return UserProfile(
                userId = userInfo.userId,
                nickname = userInfo.nickname,
                profileImage = userInfo.profileImg,
                description = userInfo.description,
                link = userInfo.link,
                interest = userInfo.interest
            )
        }
    }
}
