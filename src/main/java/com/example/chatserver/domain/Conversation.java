package com.example.chatserver.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "conversations")
public class Conversation {
  @Id private UUID id;
  @Column(nullable=false) private String type; // DM/GROUP
  private String title;

  public Conversation() {}
  public Conversation(UUID id, String type, String title) { this.id=id; this.type=type; this.title=title; }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
}
