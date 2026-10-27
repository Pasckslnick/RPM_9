package com.example.userservice.service;

import com.example.userservice.model.UserRoleModel;
import com.example.userservice.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public List<UserRoleModel> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserRoleModel getRoleById(Integer id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Роль с ID " + id + " не найдена"
                ));
    }

    @Transactional(readOnly = true)
    public UserRoleModel getRoleByName(String roleName) {
        return userRoleRepository.findByRoleNameIgnoreCase(roleName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Роль " + roleName + " не найдена"
                ));
    }

    public UserRoleModel createRole(UserRoleModel role) {
        userRoleRepository.findByRoleNameIgnoreCase(role.getRoleName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такая роль уже существует"
                    );
                });

        return userRoleRepository.save(role);
    }

    public UserRoleModel updateRole(Integer id, UserRoleModel updatedRole) {
        UserRoleModel existing = getRoleById(id);

        userRoleRepository.findByRoleNameIgnoreCase(updatedRole.getRoleName())
                .filter(role -> !role.getId().equals(id))
                .ifPresent(role -> {
                    throw new IllegalArgumentException(
                            "Такая роль уже существует"
                    );
                });

        existing.setRoleName(updatedRole.getRoleName());

        return userRoleRepository.save(existing);
    }

    public void deleteRole(Integer id) {
        UserRoleModel role = getRoleById(id);
        userRoleRepository.delete(role);
    }
}
