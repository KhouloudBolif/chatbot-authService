package com.chatbot.Auth.SecurityConfig;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void addTokenToBlacklist(String token, Date expiration) {
        blacklist.put(token, expiration);
    }

    public boolean isTokenBlacklisted(String token) {
        Date expiration = blacklist.get(token);
        return expiration != null && expiration.after(new Date());
    }
}
