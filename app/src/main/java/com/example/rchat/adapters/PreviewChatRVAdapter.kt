package com.example.rchat.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests
import com.example.rchat.windows.ChatItselfWindow

/* Класс-адаптер для единичного элемента - превью чата
*/
class PreviewChatRVAdapter(private var arrayList: ArrayList<PreviewChatDataClass>) :
    RecyclerView.Adapter<PreviewChatRVAdapter.ViewHolder>() {
    var userID = 0
    var aAdapterPosition = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mLogin: TextView = itemView.findViewById(R.id.PC_LoginTV)
        val mTime: TextView = itemView.findViewById(R.id.PC_ReceivingTimeTV)
        val mMessage: TextView = itemView.findViewById(R.id.PC_MessageTV)
        val mInfoTxt: TextView = itemView.findViewById(R.id.PC_ShownMessageInfoTV)
        var chatId = 0

        init {
            userID = ChatFunctions().getSavedUserID(itemView.context)

            /* Переход в чат по нажатию на чат
            */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatItselfWindow::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                ChatSingleton.apply {
                    isInChat = true
                    chatName = mLogin.text.toString()
                }
                if (mInfoTxt.text != itemView.context.getString(R.string.you_title))
                    mInfoTxt.visibility = View.GONE
                itemView.context.startActivity(intent)
            }

            /* Долгое нажатие на чат
            */
            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete_chat_item -> {  /* Удаление чата */
                            for (el in ChatSingleton.chatsArrayList.indices) {
                                if (ChatSingleton.chatsArrayList[el].chatId == chatId) {
                                    aAdapterPosition = el
                                    break
                                }
                            }
                            showAlertMessage(itemView.context, chatId)
                            true
                        }
                        R.id.mute_chat_item -> { /* Отключение уведомлений от чата */
                            Toast.makeText(itemView.context, itemView.context.getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.more_pc_menu)
                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popupMenu)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception) {
                    Log.e("Main", "Error of showing popup menu icons")
                } finally {
                    popupMenu.show()
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.preview_chat_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            mLogin.text = arrayList[position].login
            mTime.text = arrayList[position].time
            mMessage.text = arrayList[position].message
            mInfoTxt.text = arrayList[position].infoTxt
            chatId = arrayList[position].chatId

            /* Для скролла длинных сообщений
            */
            mMessage.isSelected = true

            /* Установка количества непрочитанных сообщений
            */
            if (arrayList[position].unreadMsgCount == 0) {
                mInfoTxt.apply {
                    if (arrayList[position].infoTxt != "") {
                        text = arrayList[position].infoTxt
                    } else
                        visibility = View.GONE
                }
            } else {
                mInfoTxt.apply {
                    text = if (arrayList[position].unreadMsgCount >= 100)
                        "99+"
                    else
                        arrayList[position].unreadMsgCount.toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    /* Функция показа предупреждающего сообщения при удалении чата
        На негативную кнопук повесил удаление чатов у всех, а на нейтральную отмену действия красоты ради - их местами не поменять
    */
    private fun showAlertMessage(context: Context, chatID: Int) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(context.getString(R.string.attention_title))
            .setMessage(context.getString(R.string.really_wanna_delete_chat_title))
            .setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.delete_for_myself_title)
            ) { _, _ ->
                Toast.makeText(context, context.getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(context.getString(R.string.delete_for_all_title)) { _, _ ->
                try {
                    Requests().delete(mapOf("id1" to userID.toString(), "id2" to chatID.toString()), "${ChatSingleton.serverUrl}/personal")
                    ChatSingleton.removeChatFromChatList(aAdapterPosition)
                }
                catch(exception: Exception) {
                    Toast.makeText(context, "ЕБАНЫЙ РОТ ЭТОГО КАЗИНО БЛЯТЬ ТЫ КТО ТАКОЙ СУКА ЧТОБЫ ЭТО ДЕЛАТЬ", Toast.LENGTH_LONG).show()
                }

            }
            .setNeutralButton(context.getString(R.string.no_title)) { dialog, _->
                dialog.cancel()
            }
        val messageWindow = message.create()
        messageWindow.show()
    }
}