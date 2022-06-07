package com.example.rchat.utils

import android.os.StrictMode
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okio.IOException


class Requests {
    private val client = OkHttpClient()
    private val request = Request.Builder()

    fun post(data: Map<String, String>, url: String): String {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val httpBuilder = url.toHttpUrlOrNull() ?: throw IOException("Bad url")
        val dataToSend = FormBody.Builder()
        data.forEach { (key, value) ->
            dataToSend.add(key, value)
        }

        client.newCall(
            request
                .post(dataToSend.build())
                .url(httpBuilder.toUrl())
                .build()
        ).execute().use { response ->
            if (!response.isSuccessful) throw IOException("${response.code}")

            return response.body!!.string()
        }
    }

    fun get(data: Map<String, String>, url: String): String {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        val httpBuilder = url.toHttpUrlOrNull() ?: throw IOException("Bad url")
        val queryData = httpBuilder.newBuilder()
        data.forEach { (key, value) ->
            queryData.addQueryParameter(key, value)
        }

        client.newCall(
            request
                .get()
                .url(queryData.build())
                .build()
        ).execute().use { response ->
            if (!response.isSuccessful) throw IOException("${response.code}")

            return response.body!!.string()
        }
    }

//    val response = client.newCall(
//        request
//            .get()
//            .url(queryData.build())
//            .build()
//    ).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            e.printStackTrace()
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            response.use {
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                ChatSingleton.func(response.body!!.string())
//            }
//        }
//    })
}