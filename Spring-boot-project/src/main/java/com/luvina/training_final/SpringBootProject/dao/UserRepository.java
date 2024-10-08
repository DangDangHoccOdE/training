package com.luvina.training_final.SpringBootProject.dao;

import com.luvina.training_final.SpringBootProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
}
