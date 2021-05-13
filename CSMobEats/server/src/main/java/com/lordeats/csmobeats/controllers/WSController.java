package com.lordeats.csmobeats.controllers;

import com.lordeats.csmobeats.services.LoginAndRegisterService;
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

    private final LoginAndRegisterService loginAndRegisterService;
    private final JSONObject acceptPayload = new JSONObject();
    private final JSONObject rejectPayload = new JSONObject();

    @Autowired
    public WSController(LoginAndRegisterService loginAndRegisterService) {
        this.loginAndRegisterService = loginAndRegisterService;

        try {
            acceptPayload.put("value","accept");
            rejectPayload.put("value", "reject");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String processMessage(String message, @Header("simpSessionId") String sessionId){

//        log.info("Received message from: " + loginAndRegisterService.getUser(sessionId) + ": " + message);
//        try {
//            if (message.startsWith("q!")) {
//                message = "User: " + loginAndRegisterService.getUser(sessionId).getString("name") + " disconnected.";
//            }
//            return loginAndRegisterService.getUser(sessionId).getString("name") + ": " + message;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "Message Error";
        return "";
    }

    @MessageMapping("/signUp")
    @SendToUser("queue/register")
    public String register(String message, @Header("simpSessionId") String sessionId) {

        try {
            JSONObject registerPayload = new JSONObject(message);
            boolean OK = loginAndRegisterService.registerUser(registerPayload);
            if(OK) {
                log.info("Registering user: " + message + " from session: " + sessionId);
                return acceptPayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rejectPayload.toString();
    }

    @MessageMapping("/signIn")
    @SendToUser("/queue/login")
    public String login(String message, @Header("simpSessionId") String sessionId) {

        log.info("Registered users: " + loginAndRegisterService.listRegisteredUsers());
        try {
            JSONObject loginPayload = new JSONObject(message);
            log.info("LoginPayload: " + loginPayload);
            boolean OK = loginAndRegisterService.logInUser(loginPayload, sessionId);
            if(OK) {
                log.info("Login user: " + message + " from session: " + sessionId);
                return acceptPayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  rejectPayload.toString();
    }
}
