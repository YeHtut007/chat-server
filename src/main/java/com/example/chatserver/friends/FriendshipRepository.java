package com.example.chatserver.friends;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
  List<Friendship> findByUserId(UUID userId);
  boolean existsByUserIdAndFriendId(UUID userId, UUID friendId);
}
