package com.lordeats.csmobeats.services;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface LoginAndRegisterService {

    ArrayList<JSONObject> registeredUsersHashMap = new ArrayList<>();
    HashMap<String, JSONObject> loginUsersHashMap = new HashMap<>();

    public boolean registerUser(JSONObject registerPayload);
    public boolean logInUser(JSONObject loginPayload, String sessionId);
    public boolean changeData(JSONObject changeDataPayload);
    public void logOutUser(String sessionId);
    public JSONObject getUser(String sessionId);
    public void removeUser(JSONObject userPayload);
    public String listRegisteredUsers();
    public String listLogInUsers();
}
