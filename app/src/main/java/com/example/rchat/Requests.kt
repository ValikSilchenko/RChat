package com.example.rchat

import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException


class Requests {
    private val client = OkHttpClient()
    private val request = Request.Builder()

    fun post(data: Map<String,String>, url: String): String {
        val httpBuilder = url.toHttpUrlOrNull() ?: throw IOException("Bad url")
        val dataToSend = FormBody.Builder()
        data.forEach{(key, value) ->
            dataToSend.add(key,value)
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

    fun get(data: Map<String,String>, url: String): String {
        val httpBuilder = url.toHttpUrlOrNull() ?: throw IOException("Bad url")
        val queryData = httpBuilder.newBuilder()
        data.forEach{(key, value) ->
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
}