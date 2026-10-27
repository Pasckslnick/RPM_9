package com.example.userservice.security;

import com.example.userservice.model.UserModel;
import com.example.userservice.model.UserRoleModel;
import com.example.userservice.repository.UserRoleRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final UserRoleRepository userRoleRepository;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            UserRoleRepository userRoleRepository
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userRoleRepository = userRoleRepository;
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");

        if (roles instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }


    public String generateToken(UserModel user) {
        UserRoleModel role = userRoleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Роль пользователя не найдена"
                ));

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", List.of(role.getRoleName()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(86400)))
                .signWith(signingKey)
                .compact();
    }
}
