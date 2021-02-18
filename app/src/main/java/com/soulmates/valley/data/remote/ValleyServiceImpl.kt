package com.soulmates.valley.data.remote

import com.soulmates.valley.ValleyApplication.Companion.globalApplication
import com.soulmates.valley.ValleyApplication.Companion.prefManager
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URISyntaxException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

object ValleyServiceImpl {
    private const val CONNECT_TIMEOUT: Long = 10
    private const val WRITE_TIMEOUT: Long = 10
    private const val READ_TIMEOUT: Long = 10
    private const val BASE_URL: String = "http://soulmates.onstove.com:8282"
    var service: ValleyService
    private var client: OkHttpClient
    private var retrofit: Retrofit
    private lateinit var socket: Socket

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        client = OkHttpClient().newBuilder().apply {
            addInterceptor(httpLoggingInterceptor)
            authenticator(
                TokenAuthenticator(
                    prefManager
                )
            )
            addNetworkInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().apply {
                    header("Authorization", prefManager.accessToken)
                }.build())
            }
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        }.build()

        retrofit = Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(client)
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
        }.build()

        service = retrofit.create(ValleyService::class.java)
    }

    fun get(): Socket {
        try {
            socket = IO.socket(BASE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return socket
    }
}