package com.example.rchat

object ChatSingleton {
    private var stroka = ""
    fun saveData(whatToSave: String) {
        stroka = whatToSave
    }

    fun getData(): String {
        return stroka
    }
}