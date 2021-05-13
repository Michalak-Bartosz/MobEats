package com.lordeats.csmobeats.listeners;

import com.lordeats.csmobeats.services.LoginAndRegisterService;
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

    private final LoginAndRegisterService loginAndRegisterService;

    @Autowired
    public SessionListener(LoginAndRegisterService loginAndRegisterService) {
        this.loginAndRegisterService = loginAndRegisterService;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event){
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("User connected with session id: " + headers.getSessionId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event){
        log.info("User disconnected with session id: " + event.getSessionId());
        loginAndRegisterService.logOutUser(event.getSessionId());
    }
}
