package com.example.rchat.utils

import org.springframework.lang.Nullable
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type

class WebSocketClient {
    private var session: StompSession? = null

    fun connect(username: String) {
        val simpleWebSocketClient: WebSocketClient = StandardWebSocketClient()

        val transports: MutableList<Transport> = ArrayList(1)
        transports.add(WebSocketTransport(simpleWebSocketClient))
        val sockJsClient =
            SockJsClient(transports)  // TODO("Didn't find (didn't read lol) class "javax.xml.stream.XMLResolver"")
        val stompClient = WebSocketStompClient(sockJsClient)

        stompClient.messageConverter = MappingJackson2MessageConverter()

        session = stompClient.connect(
            "${ChatSingleton.serverUrl}/ws",
            object : StompSessionHandlerAdapter() {
                override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                    session.subscribe("/chatTopic/$username/", object : StompFrameHandler {
                        override fun getPayloadType(headers: StompHeaders): Type {
                            return Map::class.java
                        }

                        override fun handleFrame(headers: StompHeaders, @Nullable payload: Any?) {
                            ChatSingleton.processMessage(payload as Map<*, *>)
                        }
                    })
                }
            }).get()
    }

    fun send(username: String, msg: String, sender: String) {
        session?.send("/app/user/$username/$sender/", msg)
    }

    fun send(recLogin: String, senderLogin: String, id: Int) {
        session?.send("/app/message/$recLogin/$senderLogin/", id)
    }

    fun disconnect() {
        session?.disconnect()
    }
}