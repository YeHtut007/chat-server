package com.example.chatserver.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "users")
public class UserAccount {
  @Id private UUID id;

  @Column(nullable=false, unique=true)
  private String username;

  @Column(name="display_name", nullable=false)
  private String displayName;

  @Column(name="password_hash")
  private String passwordHash;   // <--- NEW

  public UserAccount() {}

  public UserAccount(UUID id, String username, String displayName) {
    this.id = id; this.username = username; this.displayName = displayName;
  }

  public UserAccount(UUID id, String username, String displayName, String passwordHash) {
    this.id = id; this.username = username; this.displayName = displayName; this.passwordHash = passwordHash;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
