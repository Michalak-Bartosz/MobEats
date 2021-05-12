package com.lordeats.csmobeats.services;

import java.util.HashMap;

public interface LoginService {

    HashMap<String, String> logInUsersHashMap = new HashMap<>();

    public boolean loginUser(String hash, String realName);
    public void removeUser(String sessionId);
    public  String getUser(String sessionId);
    public String listUsers();
}
