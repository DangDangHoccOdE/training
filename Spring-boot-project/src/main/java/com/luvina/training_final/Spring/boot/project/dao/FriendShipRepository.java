package com.luvina.training_final.Spring.boot.project.dao;

import com.luvina.training_final.Spring.boot.project.entity.FriendShip;
import com.luvina.training_final.Spring.boot.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "friendships")
public interface FriendShipRepository extends JpaRepository<FriendShip,Long> {
    // Find the friendship relationship between two users
    Optional<FriendShip> findByUser1AndUser2(User user1, User user2);
}
