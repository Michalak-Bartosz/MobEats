package com.lordeats.csmobeats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WSController {

    private final RegistrationService registrationService;
//    private final SimpMessagingTemplate template;

    @Autowired
    public WSController(RegistrationService registrationService) {
        this.registrationService = registrationService;
//        this.template = template;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String processMessage(String message, @Header("simpSessionId") String sessionId){

        log.info("Received message from: " + registrationService.getUser(sessionId) + ": " + message);

        if (message.startsWith("q!")) {
            message = "User: " + registrationService.getUser(sessionId) + " disconnected.";
        }
        return registrationService.getUser(sessionId) + ": " + message;
    }

    @MessageMapping("/signUp")
    @SendToUser("queue/register")
    public String register(String message, @Header("simpSessionId") String sessionId) {

        if(registrationService.registerUser(sessionId, message)) {
            log.info("Registering user: " + message + " from session: " + sessionId);
            return "ture";
        } else{
            return "false";
        }
//        log.info("List users:\n" + registrationService.listUsers());
//        template.convertAndSendToUser(message,sessionId,registrationService.listUsers());
    }
}
