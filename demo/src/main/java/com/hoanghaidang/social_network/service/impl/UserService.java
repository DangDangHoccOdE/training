package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.*;

import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.IEmailService;
import com.hoanghaidang.social_network.service.inter.IUserService;
import com.hoanghaidang.social_network.utils.ConvertStringToDate;
import com.hoanghaidang.social_network.utils.ExcelGenerator;
import com.hoanghaidang.social_network.utils.GetOtp;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            throw new CustomException("Email is exists",HttpStatus.BAD_REQUEST);
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
            throw new CustomException("User has been activated", HttpStatus.BAD_REQUEST);
        }
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(new Notice("Active User completed"));
    }

    public ResponseEntity<Notice> login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

        if(authentication.isAuthenticated()){
            String otp = GetOtp.generateOtp(6); // 6 char

            // Save otp in redis with TTL 5min
            stringRedisTemplate.opsForValue().set(loginDto.getEmail(),otp,5,TimeUnit.MINUTES);
            return ResponseEntity.ok(new Notice("OTP: "+otp));
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
    public ResponseEntity<UserDto> updateProfile(String email,UserDto userDto,Authentication authentication) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(!user.isActive()){
            throw new CustomException("User has not been activated",HttpStatus.BAD_REQUEST);
        }

        User auth =  userRepository.findByEmail(authentication.getName())
                        .orElseThrow(()->new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(auth!=user){
            throw new AccessDeniedException("You have not access");
        }
        user.setLastName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setJob(userDto.getJob());
        user.setDateOfBirth(ConvertStringToDate.convert(userDto.getDateOfBirth()));
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());
        user.setAvatar(userDto.getAvatar());
        userRepository.save(user);
        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity<ApiResponse> forgetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("User is not found",HttpStatus.NOT_FOUND));

        String token = jwtService.generateToken(user.getEmail());
        // Save token -> redis TTL about 5 min
        stringRedisTemplate.opsForValue().set(token,user.getEmail(),5, TimeUnit.MINUTES);
        String link = "http://localhost:8080/api/user/change_password/"+email;
        return ResponseEntity.ok(new ApiResponse(link,token));
    }

    @Override
    public ResponseEntity<Notice> changePassword(String email,String token, String newPassword) {
        String tokenRedis = stringRedisTemplate.opsForValue().get(token);

        if (tokenRedis == null) {
            throw new CustomException("Token is invalid or expired",HttpStatus.UNAUTHORIZED);
        }

        String emailToken = jwtService.extractEmail(email,JwtService.SECRET_ACCESS_TOKEN);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User is not found",HttpStatus.NOT_FOUND));

        if(!emailToken.equals(user.getEmail())){
            throw new CustomException("User information does not match",HttpStatus.BAD_REQUEST);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete token
        stringRedisTemplate.delete(token);
        return ResponseEntity.ok(new Notice("Change password successful"));
    }
}
