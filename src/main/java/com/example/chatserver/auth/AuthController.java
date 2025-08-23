package com.example.chatserver.auth;

import com.example.chatserver.auth.dto.LoginRegisterDtos.*;
import com.example.chatserver.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final InMemoryUserRepo repo;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;

  public AuthController(InMemoryUserRepo repo, BCryptPasswordEncoder encoder, JwtService jwt) {
    this.repo = repo; this.encoder = encoder; this.jwt = jwt;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    if (repo.findByUsername(req.username()).isPresent())
      return ResponseEntity.badRequest().body("username exists");
    var user = repo.save(req.username(), req.displayName(), encoder.encode(req.password()));
    return ResponseEntity.ok(new UserDto(user.id(), user.username(), user.displayName()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
    var userOpt = repo.findByUsername(req.username());
    if (userOpt.isEmpty()) return ResponseEntity.status(401).build();
    var user = userOpt.get();
    if (!encoder.matches(req.password(), user.passwordHash())) return ResponseEntity.status(401).build();
    var token = jwt.generate(user.username());
    return ResponseEntity.ok(new LoginResponse(token, new UserDto(user.id(), user.username(), user.displayName())));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(@RequestHeader("Authorization") String auth) {
    var username = jwt.extractUsername(auth.replace("Bearer ", ""));
    var user = repo.findByUsername(username).orElseThrow();
    return ResponseEntity.ok(new UserDto(user.id(), user.username(), user.displayName()));
  }
}
