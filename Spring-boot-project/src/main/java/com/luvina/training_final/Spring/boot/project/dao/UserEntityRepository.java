package com.luvina.training_final.Spring.boot.project.dao;

import com.luvina.training_final.Spring.boot.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/user_details")
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
}
