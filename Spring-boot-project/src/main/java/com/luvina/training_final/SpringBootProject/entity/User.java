package com.luvina.training_final.SpringBootProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "job")
    private String job;

    @Column(name = "gender",nullable = false)
    private String gender;

    @Lob
    @Column(name = "avatar",columnDefinition = "TEXT")
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.REFRESH,CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "userId"),inverseJoinColumns = @JoinColumn(name = "roleId"))
    private List<Role> roles;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private List<Like> likes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "user1",cascade = CascadeType.ALL)
    private List<FriendShip> friendShipsSent;

    @OneToMany(mappedBy = "user2",cascade = CascadeType.ALL)
    private List<FriendShip> friendShipsReceived;
}
