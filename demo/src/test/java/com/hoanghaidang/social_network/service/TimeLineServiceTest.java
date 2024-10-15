package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.service.impl.TimeLineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class TimeLineServiceTest {
    @InjectMocks
    private TimeLineService timeLineService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private Pageable pageable;

    private User user;
    private Page<Post> posts;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .email("a@gmail.com")
                .build();
        Post post = Post.builder()
                .user(user)
                .content("abcs")
                .build();
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        posts = new PageImpl<>(postList);
    }

    @Test
    void testTimeline_Success(){
        int page = 0;
        int size = 5;
        pageable = PageRequest.of(page, size);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(postRepository.findFriendPostsByEmail(user.getEmail(),pageable)).thenReturn(posts);

        ResponseEntity<?> response = timeLineService.timeline(user.getEmail(),page,size);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());

    }
}
