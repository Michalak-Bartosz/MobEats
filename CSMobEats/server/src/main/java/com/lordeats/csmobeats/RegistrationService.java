package com.lordeats.csmobeats;


import java.util.HashMap;

public interface RegistrationService {

    HashMap<String, String> users = new HashMap<>();

    public boolean registerUser(String hash, String realName);
    public void removeUser(String sessionId);
    public  String getUser(String sessionId);
    public String listUsers();
}
