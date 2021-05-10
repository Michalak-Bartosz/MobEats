package com.lordeats.csmobeats;

import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService{

    @Override
    public synchronized void registerUser(String hash, String realName) {
        if(users.containsValue(realName)){
            throw new IllegalArgumentException();
        }
        users.put(hash, realName);
    }

    @Override
    public void removeUser(String sessionId) {
        users.remove(sessionId);
    }

    @Override
    public String getUser(String sessionId) {
        return users.get(sessionId);
    }

    @Override
    public String listUsers() {
        var sb = new StringBuilder();

        for(var value: users.values()){
            sb.append(value).append("\n");
        }

        return sb.toString();
    }
}
