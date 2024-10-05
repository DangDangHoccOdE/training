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
import com.luvina.training_final.Spring.boot.project.exception.ErrorDetails;
import com.luvina.training_final.Spring.boot.project.service.inter.IUserEntityService;
import com.luvina.training_final.Spring.boot.project.utils.ConvertStringToDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserEntityService implements IUserEntityService {
    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseEntity<?> registerUser(RegistrationDto registrationDto){
        Account account = accountRepository.findAccountsByEmail(registrationDto.getAccount().getEmail());

        AccountDto accountDto = registrationDto.getAccount();
        UserEntityDto userEntityDto = registrationDto.getUser();
        if(account != null){
            throw new BadRequestException("Email is exists");
        }

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

        UserEntity user = null;
        try {
            user = UserEntity.builder()
                    .firstName(userEntityDto.getFirstName())
                    .lastName(userEntityDto.getLastName())
                    .gender(userEntityDto.getGender())
                    .roles(role)
                    .dateOfBirth(ConvertStringToDate.convert(userEntityDto.getDateOfBirth()))
                    .facebookLink(userEntityDto.getFacebookLink())
                    .account(accountSave)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        accountRepository.save(accountSave);
        userEntityRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
