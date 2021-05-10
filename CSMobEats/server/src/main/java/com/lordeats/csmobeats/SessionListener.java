package com.lordeats.csmobeats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SessionListener {

    RegistrationService service;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event){
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("User connected with session id: " + headers.getSessionId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event){
        log.info("User: " + service.getUser(event.getSessionId())+ " disconnected with session id: " + event.getSessionId());
    }
}
