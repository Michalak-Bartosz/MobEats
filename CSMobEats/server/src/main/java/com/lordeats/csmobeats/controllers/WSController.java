package com.lordeats.csmobeats.controllers;

import com.lordeats.csmobeats.services.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WSController {

    private final RegistrationService registrationService;
    private final JSONObject acceptRegister = new JSONObject();
    private final JSONObject rejectRegister = new JSONObject();
    private JSONObject registerPayload;
    private JSONObject loginPayload;

    @Autowired
    public WSController(RegistrationService registrationService) {
        this.registrationService = registrationService;

        try {
            acceptRegister.put("value","accept");
            rejectRegister.put("value", "reject");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        try {
            registerPayload = new JSONObject(message);
            boolean OK = registrationService.registerUser(sessionId, registerPayload.getString("nickname"));
            if(OK) {
                log.info("Registering user: " + message + " from session: " + sessionId + "\n" + OK);
                return acceptRegister.toString();
            } else {
                return rejectRegister.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rejectRegister.toString();
    }

    @MessageMapping("/singIn")
    @SendToUser("/queue/login")
    public String login(String message, @Header("simpSessionId") String sessionId) {
        try {
            loginPayload = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  loginPayload.toString();
    }
}
