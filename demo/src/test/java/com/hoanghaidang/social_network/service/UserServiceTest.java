package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.request.UserRequestDto;
import com.hoanghaidang.social_network.dto.response.*;
import com.hoanghaidang.social_network.dto.request.LoginDto;
import com.hoanghaidang.social_network.dto.request.RegistrationDto;
import com.hoanghaidang.social_network.dto.request.UserDto;
import com.hoanghaidang.social_network.entity.Role;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.FriendStatus;
import com.hoanghaidang.social_network.enums.GenderEnum;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.UserMapper;
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
    private LikePostRepository likePostRepository;
    @Mock
    private LikeCommentRepository likeCommentRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private UserMapper userMapper;
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
                .gender(GenderEnum.FEMALE)
                .dateOfBirth("09/07/2004")
                .email("a@gmail.com")
                .password(bCryptPasswordEncoder.encode("Dang972004@"))
                .build();
        userDto = UserDto.builder()
                .gender(GenderEnum.FEMALE)
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
    void testGetUserById_Success() {
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

        UserResponse userResponse = userMapper.toUserResponse(user);

        ResponseEntity<ApiResponse<UserResponse>> response = userService.getUserById(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testGetUserById_FailUserNotFound() {
        when(userRepository.findUserById(user.getId())).thenThrow( new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> userService.getUserById(user.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User is not found", Objects.requireNonNull(exception.getMessage()));
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

        ResponseEntity<ApiResponse<JwtResponse>> response = userService.refreshToken(authentication, refreshToken);

        assertEquals(new JwtResponse(newAccessToken, newRefreshToken), Objects.requireNonNull(response.getBody()).getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRefreshToken_FailRefreshTokenIsNotValid() {
        String refreshToken = "Invalid-Token";

        mockAuthenticationAndUser(user);
        when(jwtService.validateRefreshToken(anyString(), eq(JwtService.SECRET_REFRESH_TOKEN))).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> userService.refreshToken(authentication, refreshToken));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("RefreshToken is not valid", exception.getMessage());
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

        ResponseEntity<ApiResponse<Void>> response = userService.registerUser(registrationDto);

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
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn("Token");

        ResponseEntity<ApiResponse<Void>> response = userService.activeUser(user.getEmail(),"Token");
        assertEquals("Active User completed", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testActiveUser_FailNotFoundUser() {
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));
        user.setActive(false);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn("Token");

        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail(),"Token"));
        assertEquals("User is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testActiveUser_FailUserIsActive() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        user.setActive(true);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn("Token");
        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail(),"Token"));
        assertEquals("User has been activated", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testActiveUser_FailTokenIsExpired() {
        user.setActive(false);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(any())).thenThrow(new CustomException("Token is expired or token is not found", HttpStatus.NOT_FOUND));
        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail(),"Token"));
        assertEquals("Token is expired or token is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testActiveUser_FailTokenIsIncorrect() {
        user.setActive(false);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn("OtherToken");
        CustomException exception = assertThrows(CustomException.class, () -> userService.activeUser(user.getEmail(),"Token"));
        assertEquals("Token is incorrect", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testReport_Success() throws IOException {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endDate = LocalDateTime.now();

        mockAuthenticationAndUser(user);
        when(postRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(commentRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(likePostRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(likeCommentRepository.countByUserIdAndCreateAtBetween(user.getId(), startDate, endDate)).thenReturn(1);
        when(friendShipRepository.countByUser1IdAndStatusAndUpdateAtBetween(user.getId(), FriendStatus.ACCEPTED, startDate, endDate)).thenReturn(1);
        when(friendShipRepository.countByUser2IdAndStatusAndUpdateAtBetween(user.getId(), FriendStatus.ACCEPTED, startDate, endDate)).thenReturn(1);

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

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(emailService).sendMessage(anyString(), anyString(), anyString(), anyString());

        // Act
        ResponseEntity<?> response = userService.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
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

        CustomException exception = assertThrows(CustomException.class,()->userService.login(loginDto));

        assertEquals(HttpStatus.UNAUTHORIZED,exception.getStatus());
        assertNotNull(exception.getMessage());
        assertEquals("Username or password is incorrect!",exception.getMessage());
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

        ResponseEntity<ApiResponse<JwtResponse>> response = userService.validOtp(otp,user.getEmail());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(userRepository).save(user);
        assertEquals("refreshToken",user.getRefreshToken());
        verify(stringRedisTemplate).delete(eq(user.getEmail()));
    }

    @Test
    void testValidOtp_FailNotIncorrect(){
        String otp = "123456";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        CustomException exception = assertThrows(CustomException.class,()->userService.validOtp(otp,user.getEmail()));

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
        assertEquals("Otp is not correct or expired!",exception.getMessage());
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

        when(userMapper.toUserResponse(any())).thenReturn(new UserResponse());
        ResponseEntity<ApiResponse<UserResponse>> response = userService.updateProfile(userDto,authentication);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
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
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        ResponseEntity<ApiResponse<ForgetPasswordResponse>> response = userService.forgetPassword(email);

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

        ResponseEntity<ApiResponse<Void>> response = userService.changePassword(email,token,newPassword);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Change password successful" , Objects.requireNonNull(response.getBody()).getMessage());
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

    @Test
    void sendEmailActive_Success(){
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();

        user.setActive(false);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Send email successfully")
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(emailService).sendMessage(anyString(), anyString(), anyString(), anyString());

        ResponseEntity<ApiResponse<Void>> response = userService.sendEmailActive(userRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void sendEmailActive_FailUserNotFound(){
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();

        user.setActive(false);

        when(userRepository.findByEmail(anyString())).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException customException = assertThrows(CustomException.class,()->userService.sendEmailActive(userRequestDto));

        assertEquals(HttpStatus.NOT_FOUND, customException.getStatus());
        assertEquals("User is not found", Objects.requireNonNull(customException.getMessage()));
    }

    @Test
    void sendEmailActive_FailUserHasBeenActivated(){
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();

        user.setActive(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        CustomException customException = assertThrows(CustomException.class,()->userService.sendEmailActive(userRequestDto));

        assertEquals(HttpStatus.CONFLICT, customException.getStatus());
        assertEquals("Account has been activated", Objects.requireNonNull(customException.getMessage()));
    }
}
