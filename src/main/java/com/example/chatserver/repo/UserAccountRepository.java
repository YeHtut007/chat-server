package com.example.chatserver.repo;

import com.example.chatserver.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByUsername(String username);
  Optional<UserAccount> findByUsernameIgnoreCase(String username);
  boolean existsByUsernameIgnoreCase(String username);

  // NEW: simple username search for “Find friends”
  List<UserAccount> findTop20ByUsernameContainingIgnoreCase(String q);
}
