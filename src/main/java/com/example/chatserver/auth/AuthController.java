package com.example.chatserver.auth;

import com.example.chatserver.auth.dto.LoginRegisterDtos.*;
import com.example.chatserver.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.chatserver.repo.UserAccountRepository;
import com.example.chatserver.domain.UserAccount;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final InMemoryUserRepo repo;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;
  private final UserAccountRepository userAccounts;

  public AuthController(InMemoryUserRepo repo, BCryptPasswordEncoder encoder, JwtService jwt,
          UserAccountRepository userAccounts) {
this.repo = repo; this.encoder = encoder; this.jwt = jwt; this.userAccounts = userAccounts;
}

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    final String uname = req.username().trim().toLowerCase();
    final String dname = req.displayName().trim();

    if (repo.findByUsername(uname).isPresent())
      return ResponseEntity.badRequest().body("username exists");

    var user = repo.save(uname, dname, encoder.encode(req.password()));

    // ensure DB user exists (idempotent)
    userAccounts.findByUsername(uname).orElseGet(() ->
        userAccounts.save(new UserAccount(java.util.UUID.randomUUID(), uname, dname))
    );

    return ResponseEntity.ok(new UserDto(user.id(), user.username(), user.displayName()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
    final String uname = req.username().trim().toLowerCase();

    var userOpt = repo.findByUsername(uname);
    if (userOpt.isEmpty()) return ResponseEntity.status(401).build();
    var user = userOpt.get();
    if (!encoder.matches(req.password(), user.passwordHash())) return ResponseEntity.status(401).build();

    // ensure DB user exists (idempotent); use displayName from memory if new
    userAccounts.findByUsername(uname).orElseGet(() ->
        userAccounts.save(new UserAccount(java.util.UUID.randomUUID(), uname, user.displayName()))
    );

    var token = jwt.generate(user.username()); // subject is lowercase
    return ResponseEntity.ok(new LoginResponse(token, new UserDto(user.id(), user.username(), user.displayName())));
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
      var username = raw.trim().toLowerCase();                    // normalize
      var ua = userAccounts.findByUsername(username).orElse(null);
      if (ua == null) {
        // if DB user missing, create it on the fly so membership etc. can work
        ua = userAccounts.save(new UserAccount(java.util.UUID.randomUUID(), username, username));
      }
      return ResponseEntity.ok(new UserDto(ua.getId(), ua.getUsername(), ua.getDisplayName()));
    } catch (Exception e) {
      return ResponseEntity.status(401).body("invalid token");
    }
  }

}
