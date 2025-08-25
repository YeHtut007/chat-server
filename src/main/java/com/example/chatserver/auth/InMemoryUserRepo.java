package com.example.chatserver.auth;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepo {
  private final Map<String, UserRecord> byUsername = new ConcurrentHashMap<>();

  public Optional<UserRecord> findByUsername(String u) {
    if (u == null) return Optional.empty();
    return Optional.ofNullable(byUsername.get(u.trim().toLowerCase()));
  }

  public UserRecord save(String username, String displayName, String passwordHash) {
    var key = username.trim().toLowerCase();
    var user = new UserRecord(UUID.randomUUID(), key, displayName, passwordHash);
    byUsername.put(key, user);
    return user;
  }
}
