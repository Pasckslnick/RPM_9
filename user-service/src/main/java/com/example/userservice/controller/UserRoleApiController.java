package com.example.userservice.controller;

import com.example.userservice.model.UserRoleModel;
import com.example.userservice.service.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class UserRoleApiController {

    private final UserRoleService userRoleService;

    public UserRoleApiController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public List<UserRoleModel> getAllRoles() {
        return userRoleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public UserRoleModel getRoleById(@PathVariable Integer id) {
        return userRoleService.getRoleById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserRoleModel createRole(
            @Valid @RequestBody UserRoleModel role
    ) {
        return userRoleService.createRole(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserRoleModel updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody UserRoleModel role
    ) {
        return userRoleService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@PathVariable Integer id) {
        userRoleService.deleteRole(id);
    }
}
