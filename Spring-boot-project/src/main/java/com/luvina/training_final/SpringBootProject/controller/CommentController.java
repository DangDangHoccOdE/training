package com.luvina.training_final.SpringBootProject.controller;

import com.luvina.training_final.SpringBootProject.dto.CommentDto;
import com.luvina.training_final.SpringBootProject.service.impl.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

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
