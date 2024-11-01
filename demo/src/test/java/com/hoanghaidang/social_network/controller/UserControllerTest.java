package com.hoanghaidang.social_network.controller;

import static org.hamcrest.CoreMatchers.is;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.ForgetPasswordResponse;
import com.hoanghaidang.social_network.dto.response.JwtResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.enums.GenderEnum;
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
                .gender(GenderEnum.FEMALE)
                .dateOfBirth("12/02/2004")
                .email("a@gmail.com")
                .password("Dang972004@")
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Register completed")
                .build();

        when(userService.registerUser(any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/user/register")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }

    @Test
    void testActiveUser_Success() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .data(null)
                .message("Active User completed")
                .build();

        when(userService.activeUser(any(),any())).thenReturn(ResponseEntity.ok(apiResponse));
        mockMvc.perform(put("/api/user/active_account?email={email}&token={token}",any(),any())
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
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
        ForgetPasswordResponse forgetPasswordResponse = ForgetPasswordResponse.builder()
                .link("http://localhost:8080/api/user/change_password/"+email)
                .token("AccessToken")
                .build();

        ApiResponse<ForgetPasswordResponse> apiResponse = ApiResponse.<ForgetPasswordResponse>builder()
                .data(forgetPasswordResponse)
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

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Change password successful")
                .build();

        when(userService.changePassword(anyString(),anyString(),anyString())).thenReturn(ResponseEntity.ok(apiResponse));

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
                .gender(GenderEnum.FEMALE)
                .address("HN")
                .avatar(null)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .firstName(userDto.getFirstName())
                .build();

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .data(userResponse)
                .build();

        when(userService.updateProfile(any(),any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(put("/api/user/update_profile")
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testSendEmailActive_Success() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("a@gmail.com")
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Send email successfully")
                .build();

        when(userService.sendEmailActive(any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/user/sendEmailActive")
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
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
                .otp("123456")
                .email("a@gmail.com")
                .build();

        ApiResponse<JwtResponse> apiResponse = ApiResponse.<JwtResponse>builder()
                .data(new JwtResponse("accessToken","refreshToken"))
                .build();

        when(userService.validOtp(anyString(),anyString())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/user/validate_otp")
                        .content(objectMapper.writeValueAsString(validateOtpDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
