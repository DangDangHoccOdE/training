package com.luvina.training_final.SpringBootProject.service.inter;

import com.luvina.training_final.SpringBootProject.dto.CommentDto;
import org.springframework.http.ResponseEntity;

public interface ICommentService {
    ResponseEntity<?> createComment(CommentDto commentDto);
    ResponseEntity<?> deleteComment(Long id);
    ResponseEntity<?> editComment(Long id,CommentDto commentDto);
}
