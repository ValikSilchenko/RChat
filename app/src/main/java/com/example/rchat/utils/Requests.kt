package com.example.rchat.utils

import android.os.StrictMode
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

/* Утилитный класс запросов на сервер
*/
class Requests {
    private val client = OkHttpClient()
    private val request = Request.Builder()

    /* Функция отправки html-запроса
        Вызывается в AuthorizationWindow.kt в методе onCreate() при нажатии кнопки входа в аккаунт
        и в RegistrationWindow.kt в методе onCreate() при нажатии на кнопку регистрации
     */
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

    /* Функция получения данных по html-запросу
        Вызывается в ChatSingleton.kt в методе processMessage(), в ChatItselfWindow.kt в методе onCreate(),
        в ChatsWindow.kt в методе onCreate() и в FindUsersWindow.kt в методе onCreate() при нажатии кнопки поиска пользователей
     */
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
}