package com.lordeats.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WSConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");
        registry.setApplicationDestinationPrefixes("/mobEats");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/app");
        registry.addEndpoint("/app").withSockJS();
        registry.addEndpoint("/signUp");
        registry.addEndpoint("/signUp").withSockJS();
        registry.addEndpoint("/signIn");
        registry.addEndpoint("/signIn").withSockJS();
        registry.addEndpoint("/changeData");
        registry.addEndpoint("/changeData").withSockJS();
        registry.addEndpoint("/deleteAccount");
        registry.addEndpoint("/deleteAccount").withSockJS();
        registry.addEndpoint("/getReservations");
        registry.addEndpoint("/getReservations").withSockJS();
        registry.addEndpoint("/deleteReservation");
        registry.addEndpoint("/deleteReservation").withSockJS();
        registry.addEndpoint("/addReservation");
        registry.addEndpoint("/addReservation").withSockJS();
    }
}
