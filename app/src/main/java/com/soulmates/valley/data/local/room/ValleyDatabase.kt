package com.soulmates.valley.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soulmates.valley.data.local.room.ValleyDatabase.Companion.DB_VERSION
import com.soulmates.valley.data.local.room.converter.Converter
import com.soulmates.valley.data.local.room.dao.BookmarkPostDao
import com.soulmates.valley.data.local.room.dao.SearchKeywordDao
import com.soulmates.valley.data.local.room.dao.UserInfoDao
import com.soulmates.valley.data.local.room.entity.BookmarkPost
import com.soulmates.valley.data.local.room.entity.SearchKeyword
import com.soulmates.valley.data.local.room.entity.UserProfile

@Database(entities = [UserProfile::class, SearchKeyword::class, BookmarkPost::class], version = DB_VERSION, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ValleyDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    abstract fun searchKeywordDao(): SearchKeywordDao
    abstract fun bookmarkPostDao(): BookmarkPostDao

    companion object {
        const val DB_VERSION = 2
        private const val DB_NAME = "valley.db"
        @Volatile
        private var INSTANCE: ValleyDatabase? = null

        fun getInstance(context: Context): ValleyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context) =
            Room.databaseBuilder(context.applicationContext, ValleyDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_TO_2)
                .build()

        private val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}