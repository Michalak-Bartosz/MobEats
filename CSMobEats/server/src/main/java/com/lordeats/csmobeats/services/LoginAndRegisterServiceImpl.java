package com.lordeats.csmobeats.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class LoginAndRegisterServiceImpl implements LoginAndRegisterService {

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
    public synchronized boolean changeData(JSONObject changeDataPayload) {
        String newNickname;
        String newPassword;
        JSONObject newUser = new JSONObject();
        try {
            if(changeDataPayload.getString("type").equals("nickname")) {
                newNickname = changeDataPayload.getString("newNickname");
                changeDataPayload.remove("newNickname");
                if(!checkNickname(newNickname)){
                    return false;
                }
                newUser.put("nickname", newNickname);
                newUser.put("password", changeDataPayload.getString("password"));
            } else if(changeDataPayload.getString("type").equals("password")) {
                newPassword = changeDataPayload.getString("newPassword");
                changeDataPayload.remove("newPassword");
                newUser.put("nickname", changeDataPayload.getString("nickname"));
                newUser.put("password", newPassword);
            } else {
                return false;
            }
            changeDataPayload.remove("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(registeredUsersHashMap.removeIf(value -> value.toString().equals(changeDataPayload.toString()))) {
            registeredUsersHashMap.add(newUser);
            return true;
        }

        return false;
    }

    private boolean checkNickname(String newNickname) {
        for(var value: registeredUsersHashMap){
            try {
                if(value.getString("nickname").equals(newNickname))
                    return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
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
