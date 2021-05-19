package com.lordeats.csmobeats.services;


import org.json.JSONObject;

import java.util.HashMap;

public interface LoginAndRegisterService {

    HashMap<String, JSONObject> loginUsersHashMap = new HashMap<>();

    public boolean registerUser(JSONObject registerPayload);
    public boolean logInUser(JSONObject loginPayload, String sessionId);
    public boolean changeData(JSONObject changeDataPayload);
    public boolean removeUser(JSONObject userPayload);
    public void logOutUser(String sessionId);
    public JSONObject getUser(String sessionId);
    public String listRegisteredUsers();
    public String listLogInUsers();
}
