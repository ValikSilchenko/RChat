package com.rchat.server.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.StompEndpointRegistry

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    @Override
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/chatTopic")
//        config.enableSimpleBroker("/channelTopic")
        config.setApplicationDestinationPrefixes("/app")
    }

    @Override
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").withSockJS()
    }


    @Bean
    fun messageHandlerMethodFactory(): MessageHandlerMethodFactory {
        val messageHandlerMethodFactory = DefaultMessageHandlerMethodFactory()
        messageHandlerMethodFactory.setMessageConverter(converter())
        return messageHandlerMethodFactory
    }

    @Bean
    fun converter(): MappingJackson2MessageConverter {
        return MappingJackson2MessageConverter()
    }
}