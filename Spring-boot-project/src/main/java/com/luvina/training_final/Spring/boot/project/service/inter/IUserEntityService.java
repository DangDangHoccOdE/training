package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import com.luvina.training_final.Spring.boot.project.dto.UserEntityDto;
import org.springframework.http.ResponseEntity;

import javax.management.relation.RoleNotFoundException;

public interface IUserEntityService {
    ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception;
}
