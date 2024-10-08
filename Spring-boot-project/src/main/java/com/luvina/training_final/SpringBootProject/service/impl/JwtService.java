package com.luvina.training_final.SpringBootProject.service.impl;

import com.luvina.training_final.SpringBootProject.entity.Account;
import com.luvina.training_final.SpringBootProject.entity.Role;
import com.luvina.training_final.SpringBootProject.entity.User;
import com.luvina.training_final.SpringBootProject.service.IUserSecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET_ACCESS_TOKEN = "dsafwerwdrwerwer01234567894234234rfsdfsdfsdfsdvfuyuytuytu0123456789";
    public static final String SECRET_REFRESH_TOKEN = "EUOIQWEUQWEJQOWIDKJASDNASDNM1231290KCMASKMDQSLKMDK323I912I3KMCADASD";
    private final long REFRESH_TOKEN_EXPIRATION = 30 * 24 * 60 * 60 * 100; // 30 days
    private final IUserSecurityService iUserSecurityService;

    @Autowired
    public JwtService(IUserSecurityService iUserSecurityService) {
        this.iUserSecurityService = iUserSecurityService;
    }

    public String generateToken(String email){
        Map<String, Object> claim = new HashMap<>();
        Account account = iUserSecurityService.findByEmail(email);
        User user = account.getUser();

        claim.put("isActive",account.isActive());
        claim.put("userId", user.getId());

        boolean isAdmin = false;
        boolean isUser = false;

        List<Role> list = user.getRoles();
        if(!list.isEmpty()){
            for(Role role : list){
                if(role.getRoleName().equals("ROLE_ADMIN")){
                    isAdmin = true;
                    break;
                }
                if(role.getRoleName().equals("ROLE_USER")){
                    isUser = true;
                    break;
                }
            }
        }

        claim.put("isAdmin",isAdmin);
        claim.put("isUser",isUser);

        return createToken(claim,email);
    }

    private String createToken(Map<String, Object> claim, String email){
        return Jwts.builder()
                .setClaims(claim)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 360000L))
                .signWith(getSignKey(JwtService.SECRET_ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token, String secret){
        return Jwts.parser().setSigningKey(getSignKey(secret)).parseClaimsJwt(token).getBody();
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimsFunction,String secret){
        Claims claims = extractAllClaims(token,secret);
        return claimsFunction.apply(claims);
    }

    public Date extractExpiration(String token, String secret){
        try{
            return extractClaims(token,Claims::getExpiration,secret);
        }catch (JwtException e){
            throw new RuntimeException("Failed to extract expiration from JWT: "+e.getMessage());
        }
    }

    private Boolean isTokenExpired(String token, String secret){
        Date expiration = extractExpiration(token,secret);
        if(expiration == null){
            throw new RuntimeException("Expiration date is null");
        }

        return expiration.before(new Date());
    }

    public String extractEmail(String token, String secret){
        return extractClaims(token,Claims::getSubject,secret);
    }

    public Boolean validateToken(String token, UserDetails userDetails, String secret){
        String email = extractEmail(token,secret);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token,secret));
    }
}
