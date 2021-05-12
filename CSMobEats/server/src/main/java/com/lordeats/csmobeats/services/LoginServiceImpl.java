package com.lordeats.csmobeats.services;

import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean loginUser(String hash, String realName) {
        return false;
    }

    @Override
    public void removeUser(String sessionId) {

    }

    @Override
    public String getUser(String sessionId) {
        return null;
    }

    @Override
    public String listUsers() {
        return null;
    }
}
