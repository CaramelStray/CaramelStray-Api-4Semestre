package com.example.tracker.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String key;
    private final String issuer;
    private final long expirationInMillis;

    public JwtService(
            @Value("${security.jwt.secret:tracker-api-jwt-secret-key-2026-secure}") String key,
            @Value("${security.jwt.issuer:com.example.tracker}") String issuer,
            @Value("${security.jwt.expiration-ms:3600000}") long expirationInMillis) {
        this.key = key;
        this.issuer = issuer;
        this.expirationInMillis = expirationInMillis;
    }

    public String generateToken(Authentication usuario) {
        Date agora = new Date();
        List<String> authorities = usuario.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(usuario.getName())
                .claim("authorities", authorities)
                .setExpiration(new Date(agora.getTime() + expirationInMillis))
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        Object authoritiesClaim = claims.get("authorities");

        List<String> authorities = authoritiesClaim instanceof List<?> claimList
                ? claimList.stream().map(String::valueOf).toList()
                : List.of();

        UserDetails userDetails = User.builder()
                .username(email)
                .password("secret")
                .authorities(authorities.toArray(String[]::new))
                .build();

        return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
    }
}
