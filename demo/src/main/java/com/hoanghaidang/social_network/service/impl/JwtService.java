package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.Role;
import com.hoanghaidang.social_network.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET_ACCESS_TOKEN = "dsafwerwdrwerwer01234567894234234rfsdfsdfsdfsdvfuyuytuytu0123456789";
    public static final String SECRET_REFRESH_TOKEN = "EUOIQWEUQWEJQOWIDKJASDNASDNM1231290KCMASKMDQSLKMDK323I912I3KMCADASD";
    private final UserRepository userRepository;

    @Autowired
    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String email){
        Map<String, Object> claim = new HashMap<>();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        claim.put("isActive", user.isActive());
        claim.put("userId", user.getId());

        boolean isUser = false;
        List<Role> list = user.getRoles();
        if (!list.isEmpty()) {
            for (Role role : list) {
                if (role.getRoleName().equals("ROLE_USER")) {
                    isUser = true;
                    break;
                }
            }
        }

        claim.put("isUser", isUser);
        // 2 hours
        long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 60 * 1000; // 2h
        return createToken(claim, email, ACCESS_TOKEN_EXPIRATION,SECRET_ACCESS_TOKEN);
    }

    private String createToken(Map<String, Object> claim, String email,long expiration,String secret){
        return Jwts.builder()
                .setClaims(claim)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email){
        Map<String,Object> claims = new HashMap<>();
        claims.put("type","refreshToken");
        claims.put("tokenId", UUID.randomUUID().toString()); // Thêm giá trị UUID ngẫu nhiên vào claims

        // 7 dáys
        long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days
        return createToken(claims,email, REFRESH_TOKEN_EXPIRATION,SECRET_REFRESH_TOKEN);
    }

    public Date extractIssuedAt(String token,String secret){
        return extractClaims(token,Claims::getIssuedAt,secret);
    }

    public String generateRefreshTokenWithIssuedAt(String email, Date issuedAt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refreshToken");
        claims.put("tokenId", UUID.randomUUID().toString());

        // 7 days expiration
        long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(issuedAt)  // Sử dụng thời gian issuedAt cũ
                .setExpiration(new Date(issuedAt.getTime() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSignKey(SECRET_REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token,String secret){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimsFunction,String secret){
        Claims claims = extractAllClaims(token,secret);
        return claimsFunction.apply(claims);
    }

    public String extractEmail(String token,String secret){
        return extractClaims(token, Claims::getSubject,secret);
    }

    public Date extractExpiration(String token,String secret){
        return extractClaims(token, Claims::getExpiration,secret);
    }

    private Boolean isTokenExpired(String token,String secret){
        return extractExpiration(token,secret).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails,String secret) {
        try {
            String email = extractEmail(token,secret);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token,secret));
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
            throw e;
        }
    }

    public Boolean validateRefreshToken(String refreshToken, String secret) {
        try {
            extractAllClaims(refreshToken, secret);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
