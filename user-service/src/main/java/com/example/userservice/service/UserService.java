package com.example.userservice.service;

import com.example.userservice.model.UserModel;
import com.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserModel> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public UserModel getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с ID " + id + " не найден"
                ));
    }

    @Transactional(readOnly = true)
    public UserModel getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь не найден"
                ));
    }

    @Transactional(readOnly = true)
    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с таким email не найден"
                ));
    }

    public UserModel createUser(UserModel user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Это имя пользователя уже занято");
        }

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Этот email уже зарегистрирован");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Пароль обязателен");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public UserModel updateUser(Integer id, UserModel updatedUser) {
        UserModel existing = getUserById(id);

        userRepository.findByUsernameIgnoreCase(updatedUser.getUsername())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new IllegalArgumentException(
                            "Это имя пользователя уже занято"
                    );
                });

        userRepository.findByEmailIgnoreCase(updatedUser.getEmail())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new IllegalArgumentException(
                            "Этот email уже зарегистрирован"
                    );
                });

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setPhone(updatedUser.getPhone());
        existing.setRoleId(updatedUser.getRoleId());
        existing.setPositionId(updatedUser.getPositionId());

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Integer id) {
        UserModel user = getUserById(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
