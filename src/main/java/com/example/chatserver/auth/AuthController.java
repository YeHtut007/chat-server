package com.example.chatserver.auth;

import com.example.chatserver.auth.dto.LoginRegisterDtos.*;
import com.example.chatserver.domain.ConversationMember;
import com.example.chatserver.domain.UserAccount;
import com.example.chatserver.repo.ConversationMemberRepository;
import com.example.chatserver.repo.ConversationRepository;
import com.example.chatserver.repo.UserAccountRepository;
import com.example.chatserver.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserAccountRepository users;
  private final ConversationRepository conversations;
  private final ConversationMemberRepository members;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;

  // Optional: auto-join newly registered users to a room (e.g., your test room)
  @Value("${app.defaultConversationId:}")
  private String defaultConversationId; // leave empty if you don't want auto-join

  public AuthController(
      UserAccountRepository users,
      ConversationRepository conversations,
      ConversationMemberRepository members,
      BCryptPasswordEncoder encoder,
      JwtService jwt) {
    this.users = users;
    this.conversations = conversations;
    this.members = members;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    final String uname = req.username().trim().toLowerCase();
    final String dname = req.displayName().trim();
    if (users.existsByUsernameIgnoreCase(uname))
      return ResponseEntity.badRequest().body("username exists");

    var ua = new UserAccount(UUID.randomUUID(), uname, dname,
        encoder.encode(req.password()));
    ua = users.save(ua);

    // Optional auto-join to default conversation
    if (defaultConversationId != null && !defaultConversationId.isBlank()) {
      try {
        var convId = UUID.fromString(defaultConversationId.trim());
        if (conversations.findById(convId).isPresent()
            && !members.existsByConversationIdAndUserId(convId, ua.getId())) {
          members.save(new ConversationMember(convId, ua.getId()));
        }
      } catch (IllegalArgumentException ignored) { /* invalid UUID in config */ }
    }

    var token = jwt.generate(uname); // subject = lowercase
    return ResponseEntity.ok(new LoginResponse(token, new UserDto(ua.getId(), ua.getUsername(), ua.getDisplayName())));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
    final String uname = req.username().trim().toLowerCase();
    var ua = users.findByUsernameIgnoreCase(uname).orElse(null);
    if (ua == null) return ResponseEntity.status(401).body("invalid credentials");
    if (ua.getPasswordHash() == null || !encoder.matches(req.password(), ua.getPasswordHash()))
      return ResponseEntity.status(401).body("invalid credentials");

    var token = jwt.generate(ua.getUsername());
    return ResponseEntity.ok(new LoginResponse(token, new UserDto(ua.getId(), ua.getUsername(), ua.getDisplayName())));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String auth) {
    if (auth == null || !auth.startsWith("Bearer ")) {
      return ResponseEntity.status(401).body("missing Authorization: Bearer <token>");
    }
    var token = auth.substring("Bearer ".length());
    try {
      var raw = jwt.extractUsername(token);
      if (raw == null) return ResponseEntity.status(401).body("invalid token");
      var uname = raw.trim().toLowerCase();
      var ua = users.findByUsernameIgnoreCase(uname).orElse(null);
      if (ua == null) return ResponseEntity.status(401).body("user not found");
      return ResponseEntity.ok(new UserDto(ua.getId(), ua.getUsername(), ua.getDisplayName()));
    } catch (Exception e) {
      return ResponseEntity.status(401).body("invalid token");
    }
  }
}
