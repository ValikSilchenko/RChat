package com.example.rchat.utils

import org.json.JSONObject

class JasonSTATHAM {
    fun zapretParsinga(data: String): List<JSONObject> {
        val indexMsg = mutableListOf<JSONObject>()
        var json = data.drop(1)
        json = json.dropLast(1)
        var counter = 0
        var startInd = 0
        json.forEachIndexed { index, char ->
            if (char == '{') counter += 1
            if (char == '}') counter -= 1
            if (index == json.length - 1) {
                indexMsg.add(JSONObject(json.substring(startInd)))
            } else if ((char == ',') && (counter == 0)) {
                indexMsg.add(JSONObject(json.substring(startInd, index)))
                startInd = index + 1
            }
        }
        return indexMsg
    }

    fun parseUsers(data: String): List<String> {
        var json = data.drop(1)
        json = json.dropLast(1)
        if (json.isEmpty())
            return listOf()
        while ("\"" in json)
            json = json.substringBefore("\"") + json.substringAfter("\"")

        return json.split(",") as MutableList<String>
    }

    fun parseMessage(Str: String): List<String> {
        val indexMsg = mutableListOf<String>()
        indexMsg.add(Str.substringBefore(' '))
        indexMsg.add(Str.substringAfter(' '))
        return indexMsg
    }
}