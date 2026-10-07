package com.example.pr3p7.security;

import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.UserRepository;
import com.example.pr3p7.repository.UserRoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, UserRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String roleName = "USER";
        if (user.getRoleId() != null) {
            roleName = roleRepository.findById(user.getRoleId())
                    .map(role -> role.getRoleName())
                    .orElse("USER");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(authority))
                .build();
    }
}