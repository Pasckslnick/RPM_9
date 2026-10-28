package com.example.userservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.model.UserModel;
import com.example.userservice.model.UserRoleModel;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserRoleRepository;
import com.example.userservice.security.JwtService;
import com.example.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserModel user = userService.getUserByUsername(
                    authentication.getName()
            );

            String token = jwtService.generateToken(user);
            log.info("Authentication succeeded for user={}", user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
        } catch (Exception e) {
            log.warn("Authentication failed for user {}", request.username());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Не удалось войти: неверные данные или ошибка авторизации"
                    ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username is already taken"));
        }

        if (userService.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email is already registered"));
        }

        UserRoleModel customerRole
                = userRoleRepository.findByRoleNameIgnoreCase("CUSTOMER")
                        .orElseThrow(() -> new IllegalStateException(
                                "Default CUSTOMER role is missing"
                        ));

        UserModel user = new UserModel();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setEmail(request.email());
        user.setPhone(request.phone() == null ? "" : request.phone());
        user.setRoleId(customerRole.getId());
        user.setPositionId(5); // Покупатель

        UserModel savedUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", savedUser.getId(),
                        "username", savedUser.getUsername(),
                        "email", savedUser.getEmail(),
                        "message", "Пользователь успешно зарегистрирован"
                ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserModel user = userService.getUserByUsername(username);

        UserRoleModel role = userRoleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new IllegalStateException(
                        "User role not found"
                ));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "phone", user.getPhone() == null ? "" : user.getPhone(),
                "role", role.getRoleName()
        ));
    }
}
