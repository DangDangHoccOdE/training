package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.JwtResponse;
import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.UserDto;
import com.hoanghaidang.social_network.dto.ApiResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Role;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.EmailService;
import com.hoanghaidang.social_network.service.impl.JwtService;
import com.hoanghaidang.social_network.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private PostRepository postRepository;
    @Mock
    private FriendShipRepository friendShipRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private EmailService emailService;

    private User user;
    private Role role;
    private RegistrationDto registrationDto;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("a@gmail.com").isActive(true).build();
        role = Role.builder().id(1L).roleName("ROLE_USER").build();
        registrationDto = RegistrationDto.builder()
                .firstName("a")
                .lastName("b")
                .gender("Nam")
                .dateOfBirth("09/07/2004")
                .email("a@gmail.com")
                .password(bCryptPasswordEncoder.encode("Dang972004@"))
                .build();
        userDto = UserDto.builder()
                .gender("Nam")
                .lastName("Hoang")
                .firstName("Dang")
                .dateOfBirth("09/07/2004")
                .build();
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testRefreshToken_Success() {
        String refreshToken = "Refresh-Token mock";
        String email = user.getEmail();
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        Date issuedAt = new Date();
        user.setRefreshToken(refreshToken.substring(14));

        mockAuthenticationAndUser(user);
        when(jwtService.validateRefreshToken(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(true);
        when(jwtService.extractEmail(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(email);
        when(jwtService.extractIssuedAt(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(issuedAt);
        when(jwtService.generateToken(anyString())).thenReturn(newAccessToken);
        when(jwtService.generateRefreshTokenWithIssuedAt(anyString(), any(Date.class))).thenReturn(newRefreshToken);
        when(userRepository.save(any(User.class))).thenReturn(user);
        ResponseEntity<?> response = userService.refreshToken(authentication, refreshToken);

        assertEquals(new JwtResponse(newAccessToken, newRefreshToken), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRefreshToken_FailRefreshTokenIsNotValid() {
        String refreshToken = "Invalid-Token";

        mockAuthenticationAndUser(user);
        when(jwtService.validateRefreshToken(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(false);

        ResponseEntity<?> response = userService.refreshToken(authentication, refreshToken);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new Notice("RefreshToken is not valid"), response.getBody());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testRefreshToken_FailUserNotFound() {
        String refreshToken = "Invalid-Token";

        when(jwtService.validateRefreshToken(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(false);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> userService.refreshToken(authentication, refreshToken));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User is not found", exception.getMessage());
    }

    @Test
    void testRefreshToken_FailAccessDenied() {
        String refreshToken = "Refresh-Token mock";
        User other = User.builder().id(2L).email("o@gmail.com").build();
        user.setRefreshToken(refreshToken.substring(14));

        mockAuthenticationAndUser(user);
        when(jwtService.validateRefreshToken(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(true);
        when(jwtService.extractEmail(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(other.getEmail());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userService.refreshToken(authentication, refreshToken));

        assertEquals("You have not access", exception.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(null);
        user.setRoles(Collections.singletonList(role));

        ResponseEntity<Notice> response = userService.registerUser(registrationDto);

        assertEquals("Register completed", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_FailUserIsExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(null);
        user.setRoles(Collections.singletonList(role));

        CustomException exception = assertThrows(CustomException.class, () -> userService.registerUser(registrationDto));

        assertEquals("Email is exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testRegisterUser_FailEmailExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(null);
        user.setRoles(Collections.singletonList(role));

        CustomException exception = assertThrows(CustomException.class, () -> userService.registerUser(registrationDto));

        assertEquals("Email is exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testRegisterUser_FailInfoValid() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(null);
        registrationDto.setDateOfBirth("15/10/2025");

        Exception exception = assertThrows(Exception.class, () -> userService.registerUser(registrationDto));

        assertNotNull(exception.getMessage());
    }

    @Test
    void testActiveUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        user.setActive(false);

        ResponseEntity<Notice> response = userService.activeUser(user.getEmail());
        assertEquals("Active User completed", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testActiveUser_FailNotFoundUser() {
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));
        user.setActive(false);

        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail()));
        assertEquals("User is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testActiveUser_FailUserIsActive() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        user.setActive(true);

        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail()));
        assertEquals("User has been activated", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testReport_Success() throws IOException {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endDate = LocalDateTime.now();

        mockAuthenticationAndUser(user);
        when(postRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(commentRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(likeRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(friendShipRepository.countByUser1IdAndStatusAndUpdateAtBetween(user.getId(), "accepted", startDate, endDate)).thenReturn(1);
        when(friendShipRepository.countByUser2IdAndStatusAndUpdateAtBetween(user.getId(), "accepted", startDate, endDate)).thenReturn(1);

        ResponseEntity<?> response = userService.report(user.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("attachment; filename=weekly_report.xlsx", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("application/vnd.ms-excel", Objects.requireNonNull(response.getHeaders().getContentType()).toString());
        assertNotNull(response.getBody());
    }

    @Test
    void testReport_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> userService.report(user.getEmail()));

        assertEquals("User is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        ResponseEntity<Notice> response = userService.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().startsWith("OTP: "));
        verify(valueOperations).set(eq(loginDto.getEmail()), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testLogin_FailAuthentication_IncorrectCredentials(){
        LoginDto loginDto = LoginDto.builder()
                .email("a@gmail.com")
                .password("Dang972004@")
                .build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<Notice> response = userService.login(loginDto);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new Notice("Username or password is incorrect!"),response.getBody());
        verify(valueOperations,never()).set(anyString(),anyString(),anyLong(),any(TimeUnit.class));
    }

    @Test
    void testValidOtp_Success(){
        String otp = "123456";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(user.getEmail())).thenReturn(otp);
        when(jwtService.generateToken(user.getEmail())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        ResponseEntity<?> response = userService.validOtp(otp,user.getEmail());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertInstanceOf(JwtResponse.class, response.getBody());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("accessToken",jwtResponse.getAccessToken());
        assertEquals("refreshToken",jwtResponse.getRefreshToken());
        verify(userRepository).save(user);
        assertEquals("refreshToken",user.getRefreshToken());
        verify(stringRedisTemplate).delete(eq(user.getEmail()));
    }

    @Test
    void testValidOtp_FailNotIncorrect(){
        String otp = "123456";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        ResponseEntity<?> response = userService.validOtp(otp,user.getEmail());

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(new Notice("Otp is not correct!"),response.getBody());
    }

    @Test
    void testValidOtp_UserNotFound(){
        String otp = "123456";
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->userService.validOtp(otp,user.getEmail()));
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
        assertEquals("User is not found",exception.getMessage());
    }

    @Test
    void testUpdateProfile_Success(){
        user.setActive(true);
        mockAuthenticationAndUser(user);

        ResponseEntity<UserDto> response = userService.updateProfile(userDto,authentication);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(userDto,response.getBody());
        verify(userRepository,times(1)).save(user);
    }

    @Test
    void testUpdateProfile_FailUserNotFound(){
        user.setActive(true);
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->userService.updateProfile(userDto,authentication));

        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
        assertEquals("User is not found",exception.getMessage());
    }

    @Test
    void testUpdateProfile_FailUserIsNotActive() {
        mockAuthenticationAndUser(user);
        user.setActive(false);

        CustomException exception = assertThrows(CustomException.class, () -> userService.updateProfile(userDto, authentication));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("User has not been activated", exception.getMessage());
    }

    @Test
    void testForgetPassword_Success(){
        String email = user.getEmail();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        String token = "Token 123";
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        ResponseEntity<ApiResponse> response = userService.forgetPassword(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testForgetPassword_FailUserNotFound(){
        String email = user.getEmail();
        when(userRepository.findByEmail(email)).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->userService.forgetPassword(email));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User is not found", exception.getMessage());
    }

    @Test
    void testChangePassword_Success(){
        String email = user.getEmail();
        String token = "Token 123456";
        String newPassword = "Dang972004@";
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));

        ResponseEntity<Notice> response = userService.changePassword(email,token,newPassword);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(new Notice("Change password successful") ,response.getBody());
        verify(userRepository).save(user);
        verify(stringRedisTemplate).delete(eq(token));
    }

    @Test
    void testChangePassword_FailTokenIsInvalid(){
        String email = user.getEmail();
        String token = "Token 123456";
        String newPassword = "Dang972004@";
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class,()->userService.changePassword(email,token,newPassword));
        assertEquals(exception.getMessage(),"Token is invalid or expired");
        assertEquals(HttpStatus.UNAUTHORIZED,exception.getStatus());
    }

    @Test
    void testChangePassword_FailUserNotFound(){
        String email = user.getEmail();
        String token = "Token 123456";
        String newPassword = "Dang972004@";
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(token);
        when(jwtService.extractEmail(anyString(), eq(JwtService.SECRET_ACCESS_TOKEN))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->userService.changePassword(email,token,newPassword));
        assertEquals(exception.getMessage(),"User is not found");
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testChangePassword_FailInfoNotMatch(){
        User other = User.builder().email("other@gmail.com").build();
        String email = user.getEmail();
        String token = "Token 123456";
        String newPassword = "Dang972004@";
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(token);
        when(jwtService.extractEmail(anyString(), eq(JwtService.SECRET_ACCESS_TOKEN))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(other));

        CustomException exception = assertThrows(CustomException.class,()->userService.changePassword(email,token,newPassword));

        assertEquals(exception.getMessage(),"Email does not match");
        assertEquals(HttpStatus.FORBIDDEN,exception.getStatus());
    }
}
