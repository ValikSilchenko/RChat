package com.example.rchat

import org.springframework.lang.Nullable
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type


class WebSocketClient(private val id: String, private val func: (input: String) -> Void) {
    private var session: StompSession? = null

    inner class MyStompSessionHandler : StompSessionHandlerAdapter() {
        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            session.subscribe("/chatTopic/$id", object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return String::class.java
                }
                override fun handleFrame(headers: StompHeaders, @Nullable payload: Any?) {
                    func(payload as String)
                }
            })
        }
    }

    fun connect(url: String) {
        val simpleWebSocketClient: WebSocketClient = StandardWebSocketClient()

        val transports: MutableList<Transport> = ArrayList(1)
        transports.add(WebSocketTransport(simpleWebSocketClient))
        val sockJsClient = SockJsClient(transports)
        val stompClient = WebSocketStompClient(sockJsClient)

        stompClient.messageConverter = StringMessageConverter()

        session = stompClient.connect(url, MyStompSessionHandler()).get()
    }

    fun send(url: String, id: String, msg: String) {
        session?.send("$url$id/", msg)  // /app/test/
    }
}