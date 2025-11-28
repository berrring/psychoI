package com.example.psycho.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Твой секретный ключ
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims) // В новой версии setClaims -> claims
                .subject(userDetails.getUsername()) // setSubject -> subject
                .issuedAt(new Date(System.currentTimeMillis())) // setIssuedAt -> issuedAt
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // setExpiration -> expiration
                .signWith(getSignInKey(), Jwts.SIG.HS256) // <-- Изменился синтаксис подписи
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ВОТ ЗДЕСЬ БЫЛА ОШИБКА. Исправлено под 0.12.5
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // parserBuilder больше нет, есть parser()
                .verifyWith(getSignInKey()) // setSigningKey заменили на verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws заменили на parseSignedClaims
                .getPayload(); // getBody заменили на getPayload
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
