package com.example.chatserver.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRegisterDtos {
  public record RegisterRequest(@NotBlank String username, @NotBlank String displayName, @NotBlank String password) {}
  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
  public record LoginResponse(String token, UserDto user) {}
  public record UserDto(java.util.UUID id, String username, String displayName) {}
}
