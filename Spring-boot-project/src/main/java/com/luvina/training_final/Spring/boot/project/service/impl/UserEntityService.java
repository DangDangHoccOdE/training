package com.luvina.training_final.Spring.boot.project.service.impl;

import com.luvina.training_final.Spring.boot.project.dao.AccountRepository;
import com.luvina.training_final.Spring.boot.project.dao.RoleRepository;
import com.luvina.training_final.Spring.boot.project.dao.UserEntityRepository;
import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import com.luvina.training_final.Spring.boot.project.dto.UserEntityDto;
import com.luvina.training_final.Spring.boot.project.entity.Account;
import com.luvina.training_final.Spring.boot.project.entity.Role;
import com.luvina.training_final.Spring.boot.project.entity.UserEntity;
import com.luvina.training_final.Spring.boot.project.exception.BadRequestException;
import com.luvina.training_final.Spring.boot.project.service.inter.IEmailService;
import com.luvina.training_final.Spring.boot.project.service.inter.IUserEntityService;
import com.luvina.training_final.Spring.boot.project.utils.ConvertStringToDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserEntityService implements IUserEntityService {
   UserEntityRepository userEntityRepository;
   AccountRepository accountRepository;
   BCryptPasswordEncoder bCryptPasswordEncoder;
   RoleRepository roleRepository;
   IEmailService iEmailService;

    @Override
    public ResponseEntity<?> registerUser(RegistrationDto registrationDto){
        Optional<Account> account = accountRepository.findAccountByEmail(registrationDto.getAccount().getEmail());
        if(account.isPresent()){
            throw new BadRequestException("Email is exists");
        }

        AccountDto accountDto = registrationDto.getAccount();
        UserEntityDto userEntityDto = registrationDto.getUser();

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

        UserEntity user ;
        try {
            user = UserEntity.builder()
                    .firstName(userEntityDto.getFirstName())
                    .lastName(userEntityDto.getLastName())
                    .gender(userEntityDto.getGender())
                    .roles(role)
                    .dateOfBirth(ConvertStringToDate.convert(userEntityDto.getDateOfBirth()))
                    .account(accountSave)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        accountRepository.save(accountSave);
        userEntityRepository.save(user);

        sendEmailActive(accountDto.getEmail());
        return ResponseEntity.ok().build();
    }

    private void sendEmailActive(String email){
        String subject = "Kích hoạt tài khoản của bạn";
        String text = "Vui lòng click vào đường dẫn sau để kích hoạt tài khoản: "+email;
        String url = "http://localhost:8080/user/active_account/"+email;
        text+= "<br/> <a href="+url+">"+url+"</a>";

        iEmailService.sendMessage("danghoangtest1@gmail.com",email,subject,text);
    }

    @Override
    public ResponseEntity<?> activeAccount(String email){
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find account with email"));

        if(account.isActive()){
            throw new BadRequestException("Account has been activated");
        }
        account.setActive(true);
        accountRepository.save(account);

        return ResponseEntity.ok().build();
    }
}
