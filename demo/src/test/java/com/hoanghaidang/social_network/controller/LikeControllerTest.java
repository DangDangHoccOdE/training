//package com.hoanghaidang.social_network.controller;
//
//import com.hoanghaidang.social_network.entity.Notice;
//import com.hoanghaidang.social_network.service.impl.LikeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import static org.hamcrest.CoreMatchers.is;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class LikeControllerTest {
//    @InjectMocks
//    private LikeController likeController;
//    @Mock
//    private LikeService likeService;
//    @Mock
//    private Authentication authentication;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setup(){
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
//    }
//
//    @Test
//    void testLikePost_Success() throws Exception {
//        Notice notice = new Notice("Like post is completed");
//        when(likeService.likePost(any(),anyLong())).thenReturn(ResponseEntity.ok(notice));
//
//        mockMvc.perform(post("/api/like/like_post")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"postId\":1}") // gắn vào để map sang được dto
//                .principal(authentication))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message",is(notice.getMessage())));
//    }
//
//    @Test
//    void testLikeComment_Success() throws Exception {
//        Notice notice = new Notice("Like comment is completed");
//        when(likeService.likeComment(any(),anyLong())).thenReturn(ResponseEntity.ok(notice));
//
//        mockMvc.perform(post("/api/like/like_comment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"commentId\":1}") // gắn vào để map sang được dto
//                        .principal(authentication))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message",is(notice.getMessage())));
//    }
//
//    @Test
//    void testUnlike_Success() throws Exception {
//        long likeId = 1;
//        Notice notice = new Notice("Delete like is completed");
//        when(likeService.unlike(any(),anyLong())).thenReturn(ResponseEntity.ok(notice));
//
//        mockMvc.perform(delete("/api/like/unlike/{likeId}",likeId)
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .content("{\"likeId\":1}") // gắn vào để map sang được dto
//                        .principal(authentication))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message",is(notice.getMessage())));
//    }
//
//
//}
