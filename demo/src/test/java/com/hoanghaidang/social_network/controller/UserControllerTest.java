package com.hoanghaidang.social_network.controller;

import static org.hamcrest.CoreMatchers.is;;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.RegistrationDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

//    @Test
//    void testRefreshToken_Success(){
//        JwtResponse jwtResponse = new JwtResponse("accessToken","refreshToken");
//        when(userService.refreshToken(any(),anyString())).thenReturn(ResponseEntity.ok(jwtResponse));
//    }

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
        Notice notice = new Notice("Active User completed");
        String email = "a@gmail.com";
        when(userService.activeUser(email)).thenReturn(ResponseEntity.ok(notice));
        mockMvc.perform(put("/api/user/active_account/{email}",email)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(notice.getMessage())));

    }
}
