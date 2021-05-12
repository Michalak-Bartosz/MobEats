package com.lordeats.csmobeats.services;


import java.util.HashMap;

public interface RegistrationService {

    HashMap<String, String> registeredUsersHashMap = new HashMap<>();

    public boolean registerUser(String hash, String realName);
    public void removeUser(String sessionId);
    public  String getUser(String sessionId);
    public String listUsers();
}
