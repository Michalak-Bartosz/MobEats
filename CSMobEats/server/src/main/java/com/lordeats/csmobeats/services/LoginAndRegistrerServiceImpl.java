package com.lordeats.csmobeats.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class LoginAndRegistrerServiceImpl implements LoginAndRegisterService {

    @Override
    public synchronized boolean registerUser(JSONObject registerPayload) {
        for(var value: registeredUsersHashMap){
            if(value.toString().equals(registerPayload.toString()))
                return false;
        }
        registeredUsersHashMap.add(registerPayload);
        return true;
    }

    @Override
    public synchronized boolean logInUser(JSONObject loginPayload, String sessionId) {

        for(var value: registeredUsersHashMap){
            if(value.toString().equals(loginPayload.toString()))
                return true;
        }
        return false;
    }

    @Override
    public void logOutUser(String sessionId) {
        loginUsersHashMap.remove(sessionId);
    }

    @Override
    public JSONObject getUser(String sessionId) {
        if(loginUsersHashMap.containsKey(sessionId)){
            return loginUsersHashMap.get(sessionId);
        }
        return null;
    }

    @Override
    public void removeUser(JSONObject userPayload) {
        registeredUsersHashMap.remove(userPayload);
    }

    @Override
    public String listRegisteredUsers() {
        var sb = new StringBuilder();

        for(var value: registeredUsersHashMap){
            sb.append(value).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String listLogInUsers() {

        var sb = new StringBuilder();

        for(var value: loginUsersHashMap.values()){
            sb.append(value).append("\n");
        }

        return sb.toString();
    }
}
