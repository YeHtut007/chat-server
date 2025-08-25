package com.example.chatserver.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  public JwtAuthFilter(JwtService jwtService) { this.jwtService = jwtService; }

  @Override
  protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
    String p = request.getServletPath();
    return p.startsWith("/api/auth/") || p.startsWith("/ws-native") || p.startsWith("/ws");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        String username = jwtService.extractUsername(token);
        if (username != null) {
          username = username.trim().toLowerCase(); // normalize
          var principal = User.withUsername(username).password("N/A").authorities("USER").build();
          var authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception ignored) {}
    }
    chain.doFilter(req, res);
  }
}