package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.exception.ErrorDetails;
import com.hoanghaidang.social_network.service.impl.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "Friendship Management", description = "APIs for managing friendship")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CommentController {
    CommentService commentService;

    @Operation(summary = "Create Comment", description = "Create Comment")
    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<?> createComment(@PathVariable("userId") long userId,
                                           @PathVariable("postId") long postId,
                                           @Validated @RequestBody CommentDto commentDto) {
        return commentService.createComment(userId,postId,commentDto);
    }

    @Operation(summary = "Edit Comment", description = "Edit Comment")
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editComment(@PathVariable Long id,@Validated @RequestBody CommentDto commentDto) {
        return commentService.editComment(id, commentDto);
    }

    @Operation(summary = "Delete Comment", description = "Delete Comment")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }
}
