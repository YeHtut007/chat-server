package com.example.chatserver.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  private final int expirationMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expirationMinutes}") int expirationMinutes) {

    this.expirationMinutes = expirationMinutes;

    SecretKey k = null;
    // Try Base64 first (recommended way to store secrets)
    try {
      byte[] b = Decoders.BASE64.decode(secret);
      k = Keys.hmacShaKeyFor(b);
    } catch (Exception ignore) { }

    // Fallback: raw bytes
    if (k == null) {
      try {
        k = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      } catch (Exception ignore) { }
    }

    // Dev-safe fallback: generate a secure key if user provided a weak secret
    if (k == null) {
      k = Keys.secretKeyFor(SignatureAlgorithm.HS256);
      System.out.println("[WARN] Provided JWT secret is weak/invalid. Generated a dev key at runtime. " +
          "Set a strong Base64 secret in env var JWT_SECRET for production.");
    }
    this.key = k;
  }
  public boolean isValid(String token) {
	  try {
	    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	    return true;
	  } catch (JwtException e) {
	    return false;
	  }
	}


  public String generate(String username) {
    var now = Instant.now();
    var exp = now.plusSeconds(expirationMinutes * 60L);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getSubject();
  }
}
