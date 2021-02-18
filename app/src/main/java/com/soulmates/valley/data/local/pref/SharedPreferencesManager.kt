package com.soulmates.valley.data.local.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CallSuper

class SharedPreferencesManager (context: Context){
    private val sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)

    var userId: String
        get() = sharedPreferences.getString(USER_ID)
        set(value) = sharedPreferences.putString(USER_ID, value)

    var accessToken: String
        get() = sharedPreferences.getString(AUTH_TOKEN)
        set(value) = sharedPreferences.putString(AUTH_TOKEN, value)

    var refreshToken: String
        get() = sharedPreferences.getString(REFRESH_TOKEN)
        set(value) = sharedPreferences.putString(REFRESH_TOKEN, value)

    var deviceToken: String
        get() = sharedPreferences.getString(DEVICE_TOKEN)
        set(value) = sharedPreferences.putString(DEVICE_TOKEN, value)

    @CallSuper
    fun clear() {
        sharedPreferences.edit()
            .remove(AUTH_TOKEN)
            .remove(REFRESH_TOKEN)
            .apply()
    }

    companion object {
        const val PREFERENCES_KEY = "preferences"
        private const val USER_ID = "user_id"
        private const val AUTH_TOKEN = "authentication_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val DEVICE_TOKEN = "device_token"

        private fun SharedPreferences.getString(key: String) =
            getString(key, "").orEmpty()

        private fun SharedPreferences.putString(key: String, value: String) =
            edit().putString(key, value).apply()
    }
}