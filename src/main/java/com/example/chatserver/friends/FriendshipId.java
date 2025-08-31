package com.example.chatserver.friends;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class FriendshipId implements Serializable {
  private UUID userId;
  private UUID friendId;

  public FriendshipId() {}

  public FriendshipId(UUID userId, UUID friendId) {
    this.userId = userId;
    this.friendId = friendId;
  }

  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }

  public UUID getFriendId() { return friendId; }
  public void setFriendId(UUID friendId) { this.friendId = friendId; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FriendshipId that)) return false;
    return Objects.equals(userId, that.userId) &&
           Objects.equals(friendId, that.friendId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, friendId);
  }
}
