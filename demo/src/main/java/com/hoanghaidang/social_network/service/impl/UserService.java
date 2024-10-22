package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.*;

import com.hoanghaidang.social_network.dto.request.LoginDto;
import com.hoanghaidang.social_network.dto.request.RegistrationDto;
import com.hoanghaidang.social_network.dto.request.ReportDto;
import com.hoanghaidang.social_network.dto.request.UserDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.JwtResponse;
import com.hoanghaidang.social_network.dto.response.LoginResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.UserMapper;
import com.hoanghaidang.social_network.service.inter.IEmailService;
import com.hoanghaidang.social_network.service.inter.IUserService;
import com.hoanghaidang.social_network.utils.ConvertStringToDate;
import com.hoanghaidang.social_network.utils.ExcelGenerator;
import com.hoanghaidang.social_network.utils.GetOtp;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService implements IUserService {
   UserRepository userRepository;
   BCryptPasswordEncoder bCryptPasswordEncoder;
   RoleRepository roleRepository;
   IEmailService iEmailService;
   AuthenticationManager authenticationManager;
   JwtService jwtService;
   StringRedisTemplate stringRedisTemplate;
   PostRepository postRepository;
   FriendShipRepository friendShipRepository;
   CommentRepository commentRepository;
   LikeRepository likeRepository;
    UserMapper userMapper;

    @Override
    public ResponseEntity<?> refreshToken(Authentication authentication,String refreshToken) {
        User auth =  userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(refreshToken!=null && refreshToken.startsWith("Refresh-Token ")){
            refreshToken = refreshToken.substring(14);
        }

        if(refreshToken!= null && jwtService.validateRefreshToken(refreshToken,JwtService.SECRET_REFRESH_TOKEN)){
            String email = jwtService.extractEmail(refreshToken,JwtService.SECRET_REFRESH_TOKEN);
            if(email!=null){
                if(!auth.getEmail().equals(email)){
                    throw new AccessDeniedException("You have not access");
                }

                if(auth.getRefreshToken().equals(refreshToken)){
                    Date issuedAt = jwtService.extractIssuedAt(refreshToken,JwtService.SECRET_REFRESH_TOKEN);

                    String newAccessToken = jwtService.generateToken(email);
                    String newRefreshToken = jwtService.generateRefreshTokenWithIssuedAt(email,issuedAt);
                    auth.setRefreshToken(newRefreshToken);
                    userRepository.save(auth);
                    return ResponseEntity.ok(new JwtResponse(newAccessToken,newRefreshToken));
                }
            }
        }
        return ResponseEntity.badRequest().body(new Notice("RefreshToken is not valid"));
    }

    @Override
    public ResponseEntity<?> report(String email) throws IOException {
        User user = userRepository.findByEmail(email)
              .orElseThrow(()-> new CustomException("User is not found", HttpStatus.NOT_FOUND));

        // Lấy ngày tuần trước:
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endDate = LocalDateTime.now();

        long userId = user.getId();
        int postCount = postRepository.countByUserIdAndCreateAtBetween(userId,startDate,endDate);
        int friendShipSenderCount = friendShipRepository.countByUser1IdAndStatusAndUpdateAtBetween(userId,"accepted",startDate,endDate);
        int friendShipReceiverCount = friendShipRepository.countByUser2IdAndStatusAndUpdateAtBetween(userId,"accepted",startDate,endDate);
        int newFriendshipCount = friendShipSenderCount + friendShipReceiverCount;
        int newCommentCount = commentRepository.countByUserIdAndCreateAtBetween(userId,startDate,endDate);
        int likeCount = likeRepository.countByUserIdAndCreateAtBetween(userId,startDate,endDate);

        ReportDto reportDto = new ReportDto(postCount,newFriendshipCount,likeCount,newCommentCount);

        ByteArrayInputStream excelFile = ExcelGenerator.generateExcel(reportDto);

        // Trả về header dưới dạng response
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition","attachment; filename=weekly_report.xlsx");

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(excelFile));
    }

    @Override
    @Transactional
    public ResponseEntity<Notice> registerUser(RegistrationDto registrationDto) {
        Optional<User> user = userRepository.findByEmail(registrationDto.getEmail());
        if(user.isPresent()){
            throw new CustomException("Email is exists",HttpStatus.CONFLICT);
        }

        Role roleFind= roleRepository.findByRoleName("ROLE_USER");

        if(roleFind == null){
            roleFind = new Role();
            roleFind.setRoleName("ROLE_USER");
            roleRepository.save(roleFind);
        }
        List<Role> role = new ArrayList<>(Collections.singletonList(roleFind));

        User userSaved ;
        try {
            userSaved = User.builder()
                    .firstName(registrationDto.getFirstName())
                    .lastName(registrationDto.getLastName())
                    .gender(registrationDto.getGender())
                    .roles(role)
                    .dateOfBirth(ConvertStringToDate.convert(registrationDto.getDateOfBirth()))
                    .email(registrationDto.getEmail())
                    .password(bCryptPasswordEncoder.encode(registrationDto.getPassword()))
                    .isActive(false)
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        userRepository.save(userSaved);

        sendEmailActive(registrationDto.getEmail()); // send email active user
        return ResponseEntity.ok(new Notice("Register completed"));
    }

    private void sendEmailActive(String email){
        String subject = "Kích hoạt tài khoản của bạn";
        String text = "Vui lòng click vào đường dẫn sau để kích hoạt tài khoản: "+email;
        String url = "http://localhost:8080/api/user/active_account/"+email;
        text+= "<br/> <a href="+url+">"+url+"</a>";

        iEmailService.sendMessage("danghoangtest1@gmail.com",email,subject,text);
    }

    @Override
    @Transactional
    public ResponseEntity<Notice> activeUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));

        if(user.isActive()){
            throw new CustomException("User has been activated", HttpStatus.CONFLICT);
        }
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(new Notice("Active User completed"));
    }

    public ResponseEntity<?> login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

        if(authentication.isAuthenticated()){
            String otp = GetOtp.generateOtp(6); // 6 char

            // Save otp in redis with TTL 5min
            stringRedisTemplate.opsForValue().set(loginDto.getEmail(),otp,5,TimeUnit.MINUTES);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setOtp(otp);
            loginResponse.setEmail(loginDto.getEmail());
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.badRequest().body(new Notice("Username or password is incorrect!"));
    }

    public ResponseEntity<?> validOtp(String otp,String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException("User is not found",HttpStatus.NOT_FOUND));

        // Get otp from redis
        String cacheOtp = stringRedisTemplate.opsForValue().get(email);
        if(cacheOtp != null && cacheOtp.equals(otp)){
            String token  = jwtService.generateToken(email);
            String refreshToken = jwtService.generateRefreshToken(email);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            stringRedisTemplate.delete(email);
            return ResponseEntity.ok(new JwtResponse(token,refreshToken));
        }

        return ResponseEntity.badRequest().body(new Notice("Otp is not correct!"));
    }

    @Override
    public ResponseEntity<UserResponse> updateProfile(UserDto userDto, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(!user.isActive()){
            throw new CustomException("User has not been activated",HttpStatus.FORBIDDEN);
        }

        user.setLastName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setJob(userDto.getJob());
        user.setDateOfBirth(ConvertStringToDate.convert(userDto.getDateOfBirth()));
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());
        user.setAvatar(userDto.getAvatar());
        userRepository.save(user);

        UserResponse userResponse = userMapper.toUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<ApiResponse> forgetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("User is not found",HttpStatus.NOT_FOUND));

        String token = UUID.randomUUID().toString();
        // Save token -> redis TTL about 5 min
        stringRedisTemplate.opsForValue().set(token,user.getEmail(),5, TimeUnit.MINUTES);
        String link = "http://localhost:8080/api/user/change_password/"+token;
        return ResponseEntity.ok(new ApiResponse(link,token));
    }

    @Override
    public ResponseEntity<Notice> changePassword(String email,String token, String newPassword) {
        String emailValid = stringRedisTemplate.opsForValue().get(token);

        if (emailValid == null) {
            throw new CustomException("Token is invalid or expired",HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(!user.getEmail().equals(emailValid)){
            throw new CustomException("Email does not match",HttpStatus.FORBIDDEN);
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete token
        stringRedisTemplate.delete(token);
        return ResponseEntity.ok(new Notice("Change password successful"));
    }
}
