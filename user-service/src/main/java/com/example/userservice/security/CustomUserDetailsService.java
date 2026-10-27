package com.example.userservice.security;

import com.example.userservice.model.UserModel;
import com.example.userservice.model.UserRoleModel;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserModel user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден"
                ));

        UserRoleModel role = userRoleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Роль пользователя не найдена"
                ));

        String roleName = role.getRoleName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roleName)
                .build();
    }
}
