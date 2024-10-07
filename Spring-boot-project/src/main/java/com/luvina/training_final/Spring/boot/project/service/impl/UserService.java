package com.luvina.training_final.Spring.boot.project.service.impl;

import com.luvina.training_final.Spring.boot.project.dao.AccountRepository;
import com.luvina.training_final.Spring.boot.project.dao.RoleRepository;
import com.luvina.training_final.Spring.boot.project.dao.UserRepository;
import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import com.luvina.training_final.Spring.boot.project.dto.UserDto;
import com.luvina.training_final.Spring.boot.project.entity.*;
import com.luvina.training_final.Spring.boot.project.exception.CustomException;
import com.luvina.training_final.Spring.boot.project.service.inter.IEmailService;
import com.luvina.training_final.Spring.boot.project.service.inter.IUserService;
import com.luvina.training_final.Spring.boot.project.utils.ConvertStringToDate;
import com.luvina.training_final.Spring.boot.project.utils.OtpGenerator;
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
   AccountRepository accountRepository;
   BCryptPasswordEncoder bCryptPasswordEncoder;
   RoleRepository roleRepository;
   IEmailService iEmailService;
   AuthenticationManager authenticationManager;
   JwtService jwtService;
   StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(RegistrationDto registrationDto){
        Optional<Account> account = accountRepository.findAccountByEmail(registrationDto.getAccount().getEmail());
        if(account.isPresent()){
            throw new CustomException("Email is exists",HttpStatus.BAD_REQUEST);
        }

        AccountDto accountDto = registrationDto.getAccount();
        UserDto userDto = registrationDto.getUser();

        Account accountSave = Account.builder()
                .email(accountDto.getEmail())
                .password(bCryptPasswordEncoder.encode(accountDto.getPassword()))
                .isActive(false)
                .build();

        Role roleFind= roleRepository.findByRoleName("ROLE_USER");

        if(roleFind == null){
            roleFind = new Role();
            roleFind.setRoleName("ROLE_USER");
            roleRepository.save(roleFind);
        }
        List<Role> role = new ArrayList<>(Collections.singletonList(roleFind));

        User user ;
        try {
            user = User.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .gender(userDto.getGender())
                    .roles(role)
                    .dateOfBirth(ConvertStringToDate.convert(userDto.getDateOfBirth()))
                    .account(accountSave)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        accountRepository.save(accountSave);
        userRepository.save(user);

        sendEmailActive(accountDto.getEmail()); // send email active account
        return ResponseEntity.ok(new Notice("Register completed"));
    }

    private void sendEmailActive(String email){
        String subject = "Kích hoạt tài khoản của bạn";
        String text = "Vui lòng click vào đường dẫn sau để kích hoạt tài khoản: "+email;
        String url = "http://localhost:8080/user/active_account/"+email;
        text+= "<br/> <a href="+url+">"+url+"</a>";

        iEmailService.sendMessage("danghoangtest1@gmail.com",email,subject,text);
    }

    @Override
    @Transactional
    public ResponseEntity<?> activeAccount(String email){
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new CustomException("Can not found account", HttpStatus.NOT_FOUND));

        if(account.isActive()){
            throw new CustomException("Account has been activated", HttpStatus.BAD_REQUEST);
        }
        account.setActive(true);
        accountRepository.save(account);

        return ResponseEntity.ok(new Notice("Active Account completed"));
    }

    public ResponseEntity<?> login(AccountDto accountDto){
          try{
              Authentication authentication = authenticationManager.authenticate(
                      new UsernamePasswordAuthenticationToken(accountDto.getEmail(),accountDto.getPassword()));

              Account account = accountRepository.findAccountByEmail(accountDto.getEmail())
                      .orElseThrow(() -> new CustomException("Cannot find account with email",HttpStatus.NOT_FOUND));

              if(authentication.isAuthenticated()){
                  String otp = OtpGenerator.generateOtp(6); // 6 char

                  account.setOtp(otp);
                  accountRepository.save(account);
                  return ResponseEntity.ok(new Notice("OTP: "+otp));
              }
          }catch (AuthenticationException e){
              System.out.println(e.getMessage());
              throw e;
          }
        return ResponseEntity.badRequest().body(new Notice("Username or password is incorrect!"));
    }

    public ResponseEntity<?> validOtp(String otp,AccountDto accountDto){
        Account account = accountRepository.findAccountByEmail(accountDto.getEmail())
                .orElseThrow(() -> new CustomException("Cannot find account with email",HttpStatus.NOT_FOUND));

        if(!account.isActive()){
            throw new CustomException("Account has not been activated",HttpStatus.BAD_REQUEST);
        }

        if(account.getOtp().equals(otp)){
            String token  = jwtService.generateToken(account.getEmail());
            return ResponseEntity.ok(new Notice("Token: "+token));
        }

        return ResponseEntity.badRequest().body(new Notice("Otp is not correct!"));
    }

    @Override
    public ResponseEntity<?> updateProfile(long id,UserDto userDto) throws Exception {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("Cannot find account with email",HttpStatus.NOT_FOUND));

        Account account = user.getAccount();
        if(!account.isActive()){
            throw new CustomException("Account has not been activated",HttpStatus.BAD_REQUEST);
        }

        user.setLastName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setJob(userDto.getJob());
        user.setDateOfBirth(ConvertStringToDate.convert(userDto.getDateOfBirth()));
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());
        user.setAvatar(userDto.getAvatar());
        userRepository.save(user);
        return ResponseEntity.ok(new Notice("Update profile completed!"));
    }

    @Override
    public ResponseEntity<?> forgetPassword(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(()-> new CustomException("The user could not be found",HttpStatus.NOT_FOUND));

        Account account = user.getAccount();
        String token = jwtService.generateToken(account.getEmail());
        // Save token -> redis TTL about 30 min
        stringRedisTemplate.opsForValue().set(token,account.getEmail(),30, TimeUnit.MINUTES);
        String link = "http://localhost:8080/user/change_password/"+id;
        return ResponseEntity.ok(new ApiResponse(link,token));
    }

    @Override
    public ResponseEntity<?> changePassword(long id,String token, String newPassword) {
        String tokenRedis = stringRedisTemplate.opsForValue().get(token);

        if (tokenRedis == null) {
            throw new IllegalArgumentException("Token is invalid or expired");
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException("The user could not be found",HttpStatus.NOT_FOUND));

        Account account = user.getAccount();
        account.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(account);

        // Delete token
        stringRedisTemplate.delete(token);
        return ResponseEntity.ok(new Notice("Change password successful"));
    }
}
