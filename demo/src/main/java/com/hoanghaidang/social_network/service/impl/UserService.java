package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dao.RoleRepository;
import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.UserDto;

import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.IEmailService;
import com.hoanghaidang.social_network.service.inter.IUserService;
import com.hoanghaidang.social_network.utils.ConvertStringToDate;
import com.hoanghaidang.social_network.utils.OtpGenerator;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception {
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
    public ResponseEntity<?> activeUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("The user could not be found", HttpStatus.NOT_FOUND));

        if(user.isActive()){
            throw new CustomException("User has been activated", HttpStatus.BAD_REQUEST);
        }
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(new Notice("Active User completed"));
    }

    public ResponseEntity<?> login(LoginDto loginDto){
          try{
              Authentication authentication = authenticationManager.authenticate(
                      new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

              if(authentication.isAuthenticated()){
                  String otp = OtpGenerator.generateOtp(6); // 6 char

                  // Save otp in redis with TTL 5min
                  stringRedisTemplate.opsForValue().set(loginDto.getEmail(),otp,5,TimeUnit.MINUTES);
                  return ResponseEntity.ok(new Notice("OTP: "+otp));
              }
          }catch (AuthenticationException e){
              System.out.println(e.getMessage());
              throw e;
          }
        return ResponseEntity.badRequest().body(new Notice("Username or password is incorrect!"));
    }

    public ResponseEntity<?> validOtp(String otp,String email){
        // Get otp from redis
        String cacheOtp = stringRedisTemplate.opsForValue().get(email);
        if(cacheOtp != null && cacheOtp.equals(otp)){
            String token  = jwtService.generateToken(email);
            return ResponseEntity.ok(new Notice("Token: "+token));
        }

        return ResponseEntity.badRequest().body(new Notice("Otp is not correct!"));
    }

    @Override
    public ResponseEntity<?> updateProfile(long id,UserDto userDto) {
        User user = userRepository.findUserById(id).get();

        if(!user.isActive()){
            throw new CustomException("User has not been activated",HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> forgetPassword(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(()-> new CustomException("The user could not be found",HttpStatus.NOT_FOUND));

        String token = jwtService.generateToken(user.getEmail());
        // Save token -> redis TTL about 5 min
        stringRedisTemplate.opsForValue().set(token,user.getEmail(),5, TimeUnit.MINUTES);
        String link = "http://localhost:8080/api/user/change_password/"+id;
        return ResponseEntity.ok(new ApiResponse(link,token));
    }

    @Override
    public ResponseEntity<?> changePassword(long id,String token, String newPassword) {
        String tokenRedis = stringRedisTemplate.opsForValue().get(token);

        if (tokenRedis == null) {
            throw new CustomException("Token is invalid or expired",HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(token);
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("The user could not be found",HttpStatus.NOT_FOUND));

        if(!email.equals(user.getEmail())){
            throw new CustomException("User information does not match",HttpStatus.BAD_REQUEST);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete token
        stringRedisTemplate.delete(token);
        return ResponseEntity.ok(new Notice("Change password successful"));
    }
}
