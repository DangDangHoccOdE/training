package com.luvina.training_final.SpringBootProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_ship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_ship_id")
    private long id;

    @Column(name = "status",nullable = false)
    private String status;

    @Column(name = "create_at",nullable = false,updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "user_id_1", referencedColumnName = "user_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id_2",referencedColumnName = "user_id")
    private User user2;

    @PreUpdate
    public void onUpdate(){
        this.updateAt = LocalDateTime.now();
    }
}
