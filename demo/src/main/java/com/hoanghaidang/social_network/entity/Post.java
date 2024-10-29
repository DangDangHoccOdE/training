package com.hoanghaidang.social_network.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private Status status;

    @Column(name = "like_count")
    private long likeCount=0;

    @Column(name = "comment_count")
    private long commentCount=0;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_path")
    private List<String> image;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.REFRESH,CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "post")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "post")
    private List<LikePost> likes;
}
