package com.luvina.training_final.Spring.boot.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "otp")
    private String otp;

    @Column(name = "isActive",nullable = false)
    private boolean isActive;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "account")
    private UserEntity userEntity;
}
