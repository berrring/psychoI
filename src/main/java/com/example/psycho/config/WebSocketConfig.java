package com.example.psycho.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Префикс для сообщений, отправляемых с клиента на сервер
        config.setApplicationDestinationPrefixes("/app");
        // Префикс для подписки (темы, куда сервер пишет сообщения)
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Точка входа для WebSocket-соединения
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Разрешаем всем (для dev)
                .withSockJS(); // Fallback для старых браузеров
    }
}
