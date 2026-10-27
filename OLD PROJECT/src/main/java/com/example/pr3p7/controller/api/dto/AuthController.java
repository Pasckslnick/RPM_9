package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.PositionModel;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.model.UserRoleModel;
import com.example.pr3p7.repository.UserRoleRepository;
import com.example.pr3p7.security.JwtService;
import com.example.pr3p7.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.pr3p7.repository.PositionRepository;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.Map;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Логин и регистрация пользователей")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          UserRoleRepository roleRepository, PositionRepository positionRepository, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Возвращает JWT токен")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtService.generateToken(authentication.getName());
        return new AuthResponse(token, "Bearer");
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserRoleModel defaultRole = roleRepository.findByRoleName("Customer")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found"));

        UserModel user = new UserModel();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoleId(defaultRole.getId());  // Используем setRoleId, т.к. поле roleId
        // Если Position обязателен – можно задать временную должность (например, с ID=1)
        // Пока оставим null, но в БД NOT NULL, поэтому нужно либо добавить в DTO поле positionId
        // Для простоты установим position = positionRepository.findById(1).orElseThrow()
        // Но лучше расширить RegisterRequest
        PositionModel defaultPosition = positionRepository.findById(5)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid position ID"));
        user.setPosition(defaultPosition);

        userService.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, "Bearer");
    }



    @GetMapping("/me")
    @Operation(summary = "Текущий пользователь", description = "Возвращает логин и роли текущего пользователя")
    public Map<String, Object> me(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(
                "username", authentication.getName(),
                "authorities", authorities,
                "isAdmin", authorities.contains("ROLE_ADMIN"),
                "isManager", authorities.contains("ROLE_MANAGER"),
                "canEditDealers", authorities.contains("ROLE_ADMIN") || authorities.contains("ROLE_MANAGER")
        );
    }

    @GetMapping("/profile")
    @Operation(summary = "Профиль текущего пользователя", description = "Расширенные данные о пользователе")
    public Map<String, Object> profile(Authentication auth) {
        UserModel user = userRepository.findByUsernameIgnoreCase(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + auth.getName()));

        String roleName = null;
        if (user.getRoleId() != null) {
            roleName = roleRepository.findById(user.getRoleId())
                    .map(r -> r.getRoleName())
                    .orElse(null);
        }

        String positionName = user.getPosition() != null
                ? user.getPosition().getPositionName()
                : null;

        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(
                "id",          user.getId(),
                "username",    user.getUsername(),
                "email",       user.getEmail(),
                "phone",       user.getPhone(),
                "roleId",      user.getRoleId(),
                "roleName",    roleName == null ? "" : roleName,
                "positionName", positionName == null ? "" : positionName,
                "authorities", authorities,
                "isAdmin",     authorities.contains("ROLE_ADMIN"),
                "isManager",   authorities.contains("ROLE_MANAGER")
        );
    }
}

