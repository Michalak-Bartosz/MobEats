package com.lordeats.api.services;

import org.json.JSONObject;

import java.util.HashMap;

public interface LoginAndRegisterService {

    HashMap<String, JSONObject> loginUsersHashMap = new HashMap<>();

    boolean registerUser(JSONObject registerPayload);
    boolean logInUser(JSONObject loginPayload, String sessionId);
    boolean changeData(JSONObject changeDataPayload);
    boolean removeUser(JSONObject userPayload);
    void logOutUser(String sessionId);
    String listLogInUsers();
}
