package com.hoanghaidang.social_network.controller;

import static org.hamcrest.CoreMatchers.is;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private Authentication authentication;
    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        String refreshToken = "Refresh-Token validToken";
        when(userService.refreshToken(any(),anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/user/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("refreshToken",refreshToken)
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        RegistrationDto registrationDto = RegistrationDto.builder()
                .firstName("a")
                .lastName("b")
                .gender("Nam")
                .dateOfBirth("12/02/2004")
                .email("a@gmail.com")
                .password("Dang972004@")
                .build();
        Notice notice = new Notice("Register completed");
        when(userService.registerUser(any())).thenReturn(ResponseEntity.ok(notice));

        mockMvc.perform(post("/api/user/register")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(notice.getMessage())));
    }

    @Test
    void testActiveUser_Success() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();
        Notice notice = new Notice("Active User completed");
        String email = "a@gmail.com";
        when(userService.activeUser(email)).thenReturn(ResponseEntity.ok(notice));
        mockMvc.perform(put("/api/user/active_account")
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(notice.getMessage())));
    }

    @Test
    void testReport_Success() throws Exception {
        String email = "a@gmail.com";
        when(userService.report(email)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/user/report")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testForgetPassword_Success() throws Exception {
        String email = "a@gmail.com";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(email)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .link("http://localhost:8080/api/user/change_password/"+email)
                .token("AccessToken")
                .build();
        when(userService.forgetPassword(anyString())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/user/forget_password")
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        Notice notice = new Notice("Change password successful");
        String email = "a@gmail.com";
        RequestForgetPasswordDto requestForgetPasswordDto = RequestForgetPasswordDto.builder()
                .token("Token")
                .newPassword("Dang972004@")
                .email(email)
                .build();

        when(userService.changePassword(anyString(),anyString(),anyString())).thenReturn(ResponseEntity.ok(notice));

        mockMvc.perform(put("/api/user/change_password")
                        .content(objectMapper.writeValueAsString(requestForgetPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("a")
                .lastName("b")
                .job("it")
                .dateOfBirth("12/02/2004")
                .gender("Nam")
                .address("HN")
                .avatar(null)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .firstName(userDto.getFirstName())
                .build();

        when(userService.updateProfile(any(),any())).thenReturn(ResponseEntity.ok(userResponse));

        mockMvc.perform(put("/api/user/update_profile")
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName",is(userDto.getFirstName())));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .email("a@gmail.com")
                .password("Dang972004@")
                .build();

        when(userService.login(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/user/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testValidateOtp_Success() throws Exception {
        ValidateOtpDto validateOtpDto = ValidateOtpDto.builder()
                .otp("123")
                .email("a@gmail.com")
                .build();

        when(userService.validOtp(anyString(),anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/user/validate_otp")
                        .content(objectMapper.writeValueAsString(validateOtpDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
