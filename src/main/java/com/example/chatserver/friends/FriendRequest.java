package com.example.chatserver.friends;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friend_request")
public class FriendRequest {

  @Id
  private UUID id;

  @Column(name = "from_user", nullable = false)
  private UUID fromUser;

  @Column(name = "to_user", nullable = false)
  private UUID toUser;

  @Column(nullable = false)
  private String status = "PENDING"; // PENDING / ACCEPTED / DECLINED / CANCELED

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "responded_at")
  private Instant respondedAt;

  public FriendRequest() {}

  public FriendRequest(UUID fromUser, UUID toUser) {
    this.id = UUID.randomUUID();
    this.fromUser = fromUser;
    this.toUser = toUser;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public UUID getFromUser() { return fromUser; }
  public void setFromUser(UUID fromUser) { this.fromUser = fromUser; }

  public UUID getToUser() { return toUser; }
  public void setToUser(UUID toUser) { this.toUser = toUser; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getRespondedAt() { return respondedAt; }
  public void setRespondedAt(Instant respondedAt) { this.respondedAt = respondedAt; }
}
