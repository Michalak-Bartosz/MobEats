package com.lordeats.api.services;

import com.lordeats.api.dtos.GetReservation;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface LoginAndRegisterService {

    HashMap<String, String> loginUsersHashMap = new HashMap<>();

    boolean registerUser(JSONObject registerPayload);
    boolean logInUser(JSONObject loginPayload, String sessionId);
    boolean changeData(JSONObject changeDataPayload);
    boolean removeUser(JSONObject userPayload);
    boolean removeReservation(JSONObject reservationPayload);
    void logOutUser(String sessionId);
    String listLogInUsers();
    GetReservation getReservation (int id);
    String userListReservations(String nickname);
}
