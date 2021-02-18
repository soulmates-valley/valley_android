package com.soulmates.valley.di

import com.soulmates.valley.data.local.pref.SharedPreferencesManager
import com.soulmates.valley.data.local.room.ValleyDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localModule = module {
    // Database
    single { SharedPreferencesManager(get()) }
    single { ValleyDatabase.getInstance(androidApplication()) }

    // DAO
    factory { get<ValleyDatabase>().userInfoDao() }
    factory { get<ValleyDatabase>().searchKeywordDao() }
    factory { get<ValleyDatabase>().bookmarkPostDao() }
}