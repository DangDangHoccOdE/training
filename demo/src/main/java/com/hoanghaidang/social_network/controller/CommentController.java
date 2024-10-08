package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.service.impl.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "Comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Operation(summary = "tạo comment", description = "Tạo 1 comment mới")
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Validated @RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editComment(@PathVariable Long id,@Validated @RequestBody CommentDto commentDto) {
        return commentService.editComment(id, commentDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }
}
