package com.example.chatserver.repo;

import com.example.chatserver.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByUsername(String username);
  Optional<UserAccount> findByUsernameIgnoreCase(String username); // <-- add this
}
