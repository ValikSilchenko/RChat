package com.example.rchat.utils

//import kotlinx.coroutines.*
import org.springframework.lang.Nullable
import org.springframework.messaging.converter.StringMessageConverter
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

    fun connect(url: String, username: String) {
//        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val simpleWebSocketClient: WebSocketClient = StandardWebSocketClient()

        val transports: MutableList<Transport> = ArrayList(1)
        transports.add(WebSocketTransport(simpleWebSocketClient))
        val sockJsClient = SockJsClient(transports)
        val stompClient = WebSocketStompClient(sockJsClient)

        stompClient.messageConverter = StringMessageConverter()

        session = stompClient.connect(url, object : StompSessionHandlerAdapter() {
            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                session.subscribe("/chatTopic/$username/", object : StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return String::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, @Nullable payload: Any?) {
                        ChatSingleton.processMessage(payload as String)
                    }
                })
            }
        }).get()
    }

    fun send(url: String, username: String, msg: String) {
        session?.send("$url$username/", msg)  // /app/test/
    }
}