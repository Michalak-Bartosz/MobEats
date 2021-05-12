package com.lordeats.csmobeats.listeners;

import com.lordeats.csmobeats.services.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SessionListener {

    private final RegistrationService registrationService;

    @Autowired
    public SessionListener(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event){
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("User connected with session id: " + headers.getSessionId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event){
        log.info("User disconnected with session id: " + event.getSessionId());
    }
}
