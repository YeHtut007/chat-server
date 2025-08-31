package com.example.chatserver.friends;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
  List<FriendRequest> findByToUserAndStatus(UUID toUser, String status);
  List<FriendRequest> findByFromUserAndStatus(UUID fromUser, String status);
  Optional<FriendRequest> findByFromUserAndToUserAndStatus(UUID fromUser, UUID toUser, String status);
}
