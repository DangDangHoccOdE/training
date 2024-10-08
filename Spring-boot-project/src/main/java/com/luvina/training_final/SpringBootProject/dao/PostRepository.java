package com.luvina.training_final.SpringBootProject.dao;

import com.luvina.training_final.SpringBootProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PostRepository extends JpaRepository<Post,Long> {
}
