package com.example.chatserver.friends;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendship")
@IdClass(FriendshipId.class)
public class Friendship {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Id
  @Column(name = "friend_id")
  private UUID friendId;

  @Column(nullable = false)
  private Instant since = Instant.now();

  public Friendship() {}

  public Friendship(UUID userId, UUID friendId) {
    this.userId = userId;
    this.friendId = friendId;
  }

  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }

  public UUID getFriendId() { return friendId; }
  public void setFriendId(UUID friendId) { this.friendId = friendId; }

  public Instant getSince() { return since; }
  public void setSince(Instant since) { this.since = since; }
}
