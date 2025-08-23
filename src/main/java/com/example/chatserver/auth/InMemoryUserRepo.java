package com.example.chatserver.auth;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepo {
  private final Map<String, UserRecord> byUsername = new ConcurrentHashMap<>();

  public Optional<UserRecord> findByUsername(String u) {
    return Optional.ofNullable(byUsername.get(u));
  }

  public UserRecord save(String username, String displayName, String passwordHash) {
    var user = new UserRecord(UUID.randomUUID(), username, displayName, passwordHash);
    byUsername.put(username, user);
    return user;
  }
}
