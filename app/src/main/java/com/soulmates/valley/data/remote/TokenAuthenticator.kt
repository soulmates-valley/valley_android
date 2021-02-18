package com.soulmates.valley.data.remote

import androidx.annotation.Nullable
import com.soulmates.valley.data.model.DefaultResponse
import com.google.gson.JsonObject
import com.soulmates.valley.data.local.pref.SharedPreferencesManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import timber.log.Timber


class TokenAuthenticator(private val prefManager: SharedPreferencesManager) : Authenticator {
    @Nullable
    override fun authenticate(route: Route?, response: Response): Request? {
        try {
            val refreshToken: String = prefManager.refreshToken
            val refreshObject = JsonObject()
            refreshObject.addProperty("refreshToken", refreshToken)
            val tokenResponse: retrofit2.Response<DefaultResponse> =
                ValleyServiceImpl.service.getNewToken(refreshObject).execute()

            if (tokenResponse.isSuccessful) {
                val userToken = tokenResponse.body() ?: return null
                prefManager.accessToken = userToken.data!!
                return newRequestWithAccessToken(response.request(), userToken.data)
            } else {
                if (tokenResponse.code() == 401) {
                    onSessionExpiration()
                }
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    onSessionExpiration()
                    return null
                }
            }
        }
        return null
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request? {
        return request.newBuilder()
            .removeHeader("Authorization")
            .header("Authorization", accessToken)
            .build()
    }

    private fun onSessionExpiration() {
        prefManager.clear()
    }
}