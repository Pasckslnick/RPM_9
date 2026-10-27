package com.example.userservice.repository;

import com.example.userservice.model.UserRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleModel, Integer> {

    Optional<UserRoleModel> findByRoleNameIgnoreCase(String roleName);
}
