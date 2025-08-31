package com.example.chatserver.service;

import com.example.chatserver.api.dto.FriendRequestDto;
import com.example.chatserver.api.dto.UserPublicDto;
import com.example.chatserver.domain.UserAccount;
import com.example.chatserver.friends.FriendRequest;
import com.example.chatserver.friends.FriendRequestRepository;
import com.example.chatserver.friends.Friendship;
import com.example.chatserver.friends.FriendshipRepository;
import com.example.chatserver.repo.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FriendsService {
  private final UserAccountRepository users;
  private final FriendRequestRepository reqs;
  private final FriendshipRepository friends;

  public FriendsService(UserAccountRepository users, FriendRequestRepository reqs, FriendshipRepository friends) {
    this.users = users; this.reqs = reqs; this.friends = friends;
  }

  private static String norm(String u) { return u == null ? null : u.trim().toLowerCase(); }
  private static UserPublicDto toPub(UserAccount u) {
    return new UserPublicDto(u.getId(), u.getUsername(), u.getDisplayName());
  }
  private FriendRequestDto toDto(FriendRequest fr) {
    var from = users.findById(fr.getFromUser()).orElseThrow();
    var to   = users.findById(fr.getToUser()).orElseThrow();
    return new FriendRequestDto(fr.getId(), toPub(from), toPub(to), fr.getStatus(), fr.getCreatedAt());
  }

  // --- Search users by username (partial, case-insensitive)
  public List<UserPublicDto> searchUsers(String q) {
    if (q == null || q.isBlank()) return List.of();
    return users.findTop20ByUsernameContainingIgnoreCase(norm(q))
        .stream().map(FriendsService::toPub).toList();
  }

  // --- Send friend request
  @Transactional
  public FriendRequestDto sendRequest(String fromUsername, String toUsername) {
    var from = users.findByUsernameIgnoreCase(norm(fromUsername)).orElseThrow();
    var to   = users.findByUsernameIgnoreCase(norm(toUsername)).orElseThrow();

    if (from.getId().equals(to.getId()))
      throw new IllegalArgumentException("cannot friend yourself");

    // already friends?
    if (friends.existsByUserIdAndFriendId(from.getId(), to.getId()))
      throw new IllegalStateException("already friends");

    // is there a pending request either direction?
    if (reqs.findByFromUserAndToUserAndStatus(from.getId(), to.getId(), "PENDING").isPresent()
     || reqs.findByFromUserAndToUserAndStatus(to.getId(), from.getId(), "PENDING").isPresent())
      throw new IllegalStateException("request already pending");

    var fr = new FriendRequest(from.getId(), to.getId()); // status=PENDING by default
    fr = reqs.save(fr);
    return toDto(fr);
  }

  // --- Accept request (create both directions in friendship)
  @Transactional
  public FriendRequestDto accept(UUID requestId, String actingUsername) {
    var me = users.findByUsernameIgnoreCase(norm(actingUsername)).orElseThrow();
    var fr = reqs.findById(requestId).orElseThrow();

    if (!fr.getToUser().equals(me.getId()))
      throw new SecurityException("not your request to accept");
    if (!"PENDING".equals(fr.getStatus()))
      throw new IllegalStateException("request is not pending");

    fr.setStatus("ACCEPTED");
    fr.setRespondedAt(Instant.now());
    reqs.save(fr);

    if (!friends.existsByUserIdAndFriendId(fr.getFromUser(), fr.getToUser())) {
      friends.save(new Friendship(fr.getFromUser(), fr.getToUser()));
    }
    if (!friends.existsByUserIdAndFriendId(fr.getToUser(), fr.getFromUser())) {
      friends.save(new Friendship(fr.getToUser(), fr.getFromUser()));
    }

    return toDto(fr);
  }

  // --- Decline request
  @Transactional
  public void decline(UUID requestId, String actingUsername) {
    var me = users.findByUsernameIgnoreCase(norm(actingUsername)).orElseThrow();
    var fr = reqs.findById(requestId).orElseThrow();

    if (!fr.getToUser().equals(me.getId()))
      throw new SecurityException("not your request to decline");
    if (!"PENDING".equals(fr.getStatus())) return;

    fr.setStatus("DECLINED");
    fr.setRespondedAt(Instant.now());
    reqs.save(fr);
  }

  // --- List my friends (public view)
  public List<UserPublicDto> listFriends(String username) {
    var me = users.findByUsernameIgnoreCase(norm(username)).orElseThrow();
    return friends.findByUserId(me.getId()).stream()
        .map(f -> users.findById(f.getFriendId()).orElseThrow())
        .map(FriendsService::toPub)
        .toList();
  }

  // --- Requests
  public List<FriendRequestDto> incoming(String username) {
    var me = users.findByUsernameIgnoreCase(norm(username)).orElseThrow();
    return reqs.findByToUserAndStatus(me.getId(), "PENDING").stream().map(this::toDto).toList();
  }
  public List<FriendRequestDto> outgoing(String username) {
    var me = users.findByUsernameIgnoreCase(norm(username)).orElseThrow();
    return reqs.findByFromUserAndStatus(me.getId(), "PENDING").stream().map(this::toDto).toList();
  }
}
