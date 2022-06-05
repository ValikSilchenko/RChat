package com.example.rchat

import android.content.Context
import androidx.appcompat.app.AlertDialog

class Functions {
    fun showMessage(titleText: CharSequence, messageText: CharSequence, context: Context) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(titleText)
            .setMessage(messageText)
            .setCancelable(true)
            .setPositiveButton(
                "Ок"
            ) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }

    fun addToList(
        previewChatLogins: MutableList<String>,
        previewChatReceivingTimes: MutableList<String>,
        previewChatMessages: MutableList<String>,
        previewLogin: String,
        previewReceivingTime: String,
        previewMessage: String
    ) {
        previewChatLogins.add(previewLogin)
        previewChatReceivingTimes.add(previewReceivingTime)
        previewChatMessages.add(previewMessage)
    }

    fun addToList(
        incomingLogins: MutableList<String>,
        incomingMessages: MutableList<String>,
        outgoingLogins: MutableList<String>,
        outgoingMessages: MutableList<String>,
        incomingLogin: String,
        incomingMessage: String,
        outgoingLogin: String,
        outgoingMessage: String
    ) {
        incomingLogins.add(incomingLogin)
        incomingMessages.add(incomingMessage)
        outgoingLogins.add(outgoingLogin)
        outgoingMessages.add(outgoingMessage)
    }
}