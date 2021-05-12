package com.lordeats.csmobeats.services;

import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Override
    public synchronized boolean registerUser(String hash, String realName) {
        if(registeredUsersHashMap.containsValue(realName)){
            return false;
        }
        registeredUsersHashMap.put(hash, realName);
        return true;
    }

    @Override
    public void removeUser(String sessionId) {
        registeredUsersHashMap.remove(sessionId);
    }

    @Override
    public String getUser(String sessionId) {
        return registeredUsersHashMap.get(sessionId);
    }

    @Override
    public String listUsers() {
        var sb = new StringBuilder();

        for(var value: registeredUsersHashMap.values()){
            sb.append(value).append("\n");
        }

        return sb.toString();
    }
}
