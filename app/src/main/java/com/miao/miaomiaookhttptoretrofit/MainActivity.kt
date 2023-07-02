package com.miao.miaomiaookhttptoretrofit

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object{
        const val TIME = 5L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun okHttpPost() {
        val url = "https://example.com/api/users"
        if (url.isBlank()) {
            return
        }
        url.toHttpUrlOrNull() ?: return

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonString = ""
        val requestBody = jsonString.toRequestBody(mediaType)

        val headers = Headers.Builder().apply {
            add("name", "value")
            add("Content-Type", "application/json")
            add("Accept", "application/json")
        }.build()

        val request = Request.Builder()
            .headers(headers)
            .post(requestBody)
            .url(url)
            .build()

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        }

        OkHttpClient.Builder()
            .connectTimeout(TIME, TimeUnit.SECONDS)
            .readTimeout(TIME, TimeUnit.SECONDS)
            .writeTimeout(TIME, TimeUnit.SECONDS)
            .build()
            .newCall(request)
            .enqueue(callback)
    }
}