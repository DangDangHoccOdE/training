package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.Role;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetails userDetails;

    private User user;
    private static final String SECRET_ACCESS_TOKEN = JwtService.SECRET_ACCESS_TOKEN;
    private static final String SECRET_REFRESH_TOKEN = JwtService.SECRET_REFRESH_TOKEN;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder().roleName("ROLE_USER").build();
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user = User.builder()
                .email("a@gmail.com")
                .roles(roles)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtService.generateToken(user.getEmail());

        assertNotNull(token);
        String extractEmail = jwtService.extractEmail(token,SECRET_ACCESS_TOKEN);
        assertEquals(user.getEmail(),extractEmail);
    }

    @Test
    void testGenerateToken_FailUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("Uer is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->jwtService.generateToken(user.getEmail()));

        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
        assertEquals(exception.getMessage(),"Uer is not found");
    }

    @Test
    void testGenerateRefreshToken_Success() {
        String token = jwtService.generateRefreshToken(user.getEmail());

        assertNotNull(token);
        assertTrue(jwtService.validateRefreshToken(token,SECRET_REFRESH_TOKEN));
    }

    @Test
    void testGenerateRefreshTokenWithIssuedAt_Success() {
        String token = jwtService.generateRefreshTokenWithIssuedAt(user.getEmail(), new Date());

        assertNotNull(token);
        assertTrue(jwtService.validateRefreshToken(token,SECRET_REFRESH_TOKEN));
    }

    @Test
    void testValidateToken_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        String token = jwtService.generateToken(user.getEmail());
        boolean valid = jwtService.validateToken(token,userDetails,SECRET_ACCESS_TOKEN);
        assertTrue(valid);
    }

    @Test
    void testExtractExpiration_Success() {
        String token = jwtService.generateToken(user.getEmail());
        Date expiration = jwtService.extractExpiration(token,SECRET_ACCESS_TOKEN);
        assertNotNull(expiration);
    }

    @Test
    void testExtractEmail_Success() {
        String token = jwtService.generateToken(user.getEmail());
        String email = jwtService.extractEmail(token, SECRET_ACCESS_TOKEN);

        assertNotNull(email);
        assertEquals(email,user.getEmail());
    }

    @Test
    void testExtractIssuedAt_Success() {
        Date now = new Date();
        String token = jwtService.generateToken(user.getEmail());

        Date date = jwtService.extractIssuedAt(token, SECRET_ACCESS_TOKEN);
        assertNotNull(date);
        assertTrue(date.getTime() < now.getTime());
    }

    @Test
    void validateToken_Success(){
        String token = jwtService.generateToken(user.getEmail());
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        boolean validToken = jwtService.validateToken(token,userDetails,SECRET_ACCESS_TOKEN);
        assertTrue(validToken);
    }

    @Test
    void validateRefreshToken_Success(){
        String token = jwtService.generateRefreshToken(user.getEmail());

        boolean validRefreshToken = jwtService.validateRefreshToken(token,SECRET_REFRESH_TOKEN);
        assertTrue(validRefreshToken);
    }

    @Test
    void validateRefreshToken_Failure(){
        String inValidToken = "token";
        boolean isValid = jwtService.validateRefreshToken(inValidToken,SECRET_REFRESH_TOKEN);
        assertFalse(isValid);
    }


}
