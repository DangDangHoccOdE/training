package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "Comment Management", description = "APIs for managing comment")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CommentController {
    CommentService commentService;

    @Operation(summary = "Create Comment", description = "Create Comment")
    @PostMapping("comment_post")
    public ResponseEntity<Notice> createComment(Authentication authentication, @Validated @RequestBody CommentDto commentDto) {
        return commentService.createComment(authentication,commentDto.getPostId(),commentDto);
    }

    @Operation(summary = "Edit Comment", description = "Edit Comment")
    @PutMapping("/edit/{id}")
    public ResponseEntity<CommentDto> editComment(@PathVariable Long id,
                                         @Validated @RequestBody CommentDto commentDto,
                                         Authentication authentication) {
        return commentService.editComment(authentication,id, commentDto);
    }

    @Operation(summary = "Delete Comment", description = "Delete Comment")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Notice> deleteComment(Authentication authentication,@PathVariable Long id) {
        return commentService.deleteComment(authentication,id);
    }
}
