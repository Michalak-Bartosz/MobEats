package com.lordeats.api.controllers;

import com.lordeats.api.dtos.GetReservation;
import com.lordeats.api.services.CustomerService;
import com.lordeats.api.services.LoginAndRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class WSController {

    private final LoginAndRegisterService loginAndRegisterService;
    private final JSONObject acceptPayload = new JSONObject();
    private final JSONObject rejectPayload = new JSONObject();
    private final JSONObject acceptNicknameChangePayload = new JSONObject();
    private final JSONObject acceptPasswordChangePayload = new JSONObject();

    @Autowired
    public WSController(LoginAndRegisterService loginAndRegisterService) {
        this.loginAndRegisterService = loginAndRegisterService;

        try {
            acceptPayload.put("value","accept");
            rejectPayload.put("value", "reject");
            acceptNicknameChangePayload.put("value","acceptNickname");
            acceptPasswordChangePayload.put("value", "acceptPassword");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/findPpl")
    @SendTo("/topic/messages")
    public String processMessage(String message, @Header("simpSessionId") String sessionId){
        try {
            JSONObject findPplPayload = new JSONObject(message);
            log.info("User: " + findPplPayload + " looking for ppl to eat.");
            findPplPayload.put("message", "Hi! I'm " + findPplPayload.getString("nickname") + " and I'm looking for someone to eat with me. Check it out");
            return findPplPayload.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Message Error";
    }

    @MessageMapping("/signUp")
    @SendToUser("queue/register")
    public String register(String message, @Header("simpSessionId") String sessionId) {

        try {
            JSONObject registerPayload = new JSONObject(message);
            log.info("RegisterPayload: " + registerPayload);
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

    @MessageMapping("/changeUserData")
    @SendToUser("/queue/changeData")
    public String changeData(String message, @Header("simpSessionId") String sessionId) {

        try {
            JSONObject changeDataPayload = new JSONObject(message);
            log.info("changeDataPayload: " + changeDataPayload);
            boolean OK = loginAndRegisterService.changeData(changeDataPayload);
            if(OK) {
                log.info("Data user changed: " + message + " from session: " + sessionId);
                if(changeDataPayload.has("newNickname"))
                    return acceptNicknameChangePayload.toString();
                if(changeDataPayload.has("newPassword"))
                    return acceptPasswordChangePayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  rejectPayload.toString();
    }

    @MessageMapping("/deleteAccount")
    @SendToUser("/queue/dellAccount")
    public String dellAccount(String message, @Header("simpSessionId") String sessionId) {
        try {
            JSONObject deleteAccountPayload = new JSONObject(message);
            log.info("Remove User Payload: " + deleteAccountPayload);
            boolean OK = loginAndRegisterService.removeUser(deleteAccountPayload);
            if(OK) {
                log.info("Data user removed: " + message + " from session: " + sessionId);
                return acceptPayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  rejectPayload.toString();
    }

    @MessageMapping("/getReservations")
    @SendToUser("/queue/getReservationsList")
    public String getReservationsList(String message, @Header("simpSessionId") String sessionId) {
        log.info("User : " + message + " want reservations list.");
        String listReservations = loginAndRegisterService.userListReservations(message);
        if(!listReservations.isEmpty()) {
            log.info("Reservations list: " + listReservations + " from session: " + sessionId);
            return listReservations;
        } else {
            return rejectPayload.toString();
        }
    }

    @MessageMapping("/deleteReservation")
    @SendToUser("/queue/dellReservation")
    public String dellReservation(String message, @Header("simpSessionId") String sessionId) {
        try {
            JSONObject deleteReservationPayload = new JSONObject(message);
            log.info("Remove Reservation Payload: " + deleteReservationPayload);
            boolean OK  = loginAndRegisterService.removeReservation(deleteReservationPayload);
            if(OK) {
                log.info("Reservation removed: " + message + " from session: " + sessionId);
                return acceptPayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rejectPayload.toString();
    }

    @MessageMapping("/addReservation")
    @SendToUser("/queue/addNewReservation")
    public String addNewReservation(String message, @Header("simpSessionId") String sessionId) {
        try {
            JSONObject addReservationPayload = new JSONObject(message);
            log.info("Add Reservation Payload: " + addReservationPayload);
            boolean OK  = loginAndRegisterService.addReservation(addReservationPayload);
            if(OK) {
                log.info("Reservation add: " + message + " from session: " + sessionId);
                return acceptPayload.toString();
            } else {
                return rejectPayload.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rejectPayload.toString();
    }
}