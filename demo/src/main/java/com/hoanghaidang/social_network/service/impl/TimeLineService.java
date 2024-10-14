package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.ITimeLineService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TimeLineService implements ITimeLineService {
    PostRepository postRepository;
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> timeline(String email, int page, int size) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new CustomException("User is not found", HttpStatus.NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findFriendPostsByEmail(user.getEmail(), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
