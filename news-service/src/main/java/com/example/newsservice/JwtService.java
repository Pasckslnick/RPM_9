package com.example.newsservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload();
    }

    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().filter(String.class::isInstance)
                    .map(String.class::cast).toList();
        }
        return List.of();
    }
}
