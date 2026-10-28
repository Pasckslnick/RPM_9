package com.example.testdrivesservice.repository;

import com.example.testdrivesservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    Optional<UserAccount> findByUsernameIgnoreCase(String username);
}
