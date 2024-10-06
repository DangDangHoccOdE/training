package com.luvina.training_final.Spring.boot.project.dao;

import com.luvina.training_final.Spring.boot.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
}
