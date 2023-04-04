package org.TouchAPI.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ApiKeyAuthenticator {
    private final Set<String> validKeys;

    public ApiKeyAuthenticator() {
        this.validKeys = new HashSet<>();
        validKeys.add("my_api_key");
    }

    public boolean isValidKey(String apiKey) {
        return validKeys.contains(apiKey);
    }
}