package com.example.viewspace.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.viewspace.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserVersionService {

    private final Map<String, Integer> userVersionCache = new ConcurrentHashMap<>();

    public Integer getJwtVersion(String username, UserEntityRepository userRepository) {
        return userVersionCache.computeIfAbsent(username, key ->
            userRepository.findByUsername(key)
                .map(u -> u.getJwtVersion() != null ? u.getJwtVersion() : 1)
                .orElse(1)
        );
    }

    public void updateJwtVersion(String username, Integer version) {
        userVersionCache.put(username, version);
    }

    public void evictUserVersion(String username) {
        userVersionCache.remove(username);
    }
}
