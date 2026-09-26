package com.restaurant.caller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.restaurant.caller.websocket.CallWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CallWebSocketHandler callWebSocketHandler;

    public WebSocketConfig(CallWebSocketHandler callWebSocketHandler) {
        this.callWebSocketHandler = callWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry) {

        registry.addHandler(
                callWebSocketHandler,
                "/media"
        ).setAllowedOrigins("*");
    }
}