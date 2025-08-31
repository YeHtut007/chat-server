package com.example.chatserver.api;

import com.example.chatserver.api.dto.FriendRequestDto;
import com.example.chatserver.api.dto.UserPublicDto;
import com.example.chatserver.service.DmService;
import com.example.chatserver.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FriendsController {

  private final FriendsService friends;
  private final DmService dm;

  public FriendsController(FriendsService friends, DmService dm) {
    this.friends = friends;
    this.dm = dm;
  }

  // --- Search users by username (partial, case-insensitive)
  // GET /api/users/search?q=ali
  @GetMapping("/users/search")
  public List<UserPublicDto> search(@RequestParam("q") String q, Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    return friends.searchUsers(q);
  }

  // --- Send a friend request
  // POST /api/friends/requests { "toUsername": "bob" }
  @PostMapping("/friends/requests")
  public FriendRequestDto send(@RequestBody Map<String, String> body, Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    String toUsername = body != null ? body.get("toUsername") : null;
    if (toUsername == null || toUsername.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toUsername is required");
    }
    try {
      return friends.sendRequest(p.getName(), toUsername);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (IllegalStateException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
  }

  // --- Incoming pending requests for me
  // GET /api/friends/requests/incoming
  @GetMapping("/friends/requests/incoming")
  public List<FriendRequestDto> incoming(Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    return friends.incoming(p.getName());
  }

  // --- Outgoing pending requests I sent
  // GET /api/friends/requests/outgoing
  @GetMapping("/friends/requests/outgoing")
  public List<FriendRequestDto> outgoing(Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    return friends.outgoing(p.getName());
  }

  // --- Accept a request that was sent to me
  // POST /api/friends/requests/{id}/accept
  @PostMapping("/friends/requests/{id}/accept")
  public FriendRequestDto accept(@PathVariable UUID id, Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    try {
      return friends.accept(id, p.getName());
    } catch (SecurityException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    } catch (IllegalStateException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
  }

  // --- Decline a request that was sent to me
  // POST /api/friends/requests/{id}/decline
  @PostMapping("/friends/requests/{id}/decline")
  public ResponseEntity<Void> decline(@PathVariable UUID id, Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    try {
      friends.decline(id, p.getName());
      return ResponseEntity.ok().build();
    } catch (SecurityException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  // --- My friends list
  // GET /api/friends
  @GetMapping("/friends")
  public List<UserPublicDto> listFriends(Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    return friends.listFriends(p.getName());
  }

  // --- Open or create a DM with a friend (returns conversationId)
  // POST /api/dm/open { "username": "bob" }
  @PostMapping("/dm/open")
  public Map<String, String> openDm(@RequestBody Map<String, String> body, Principal p) {
    if (p == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    String peer = body != null ? body.get("username") : null;
    if (peer == null || peer.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
    }
    var convId = dm.openOrCreateDm(p.getName(), peer);
    return Map.of("conversationId", convId.toString());
  }
}
