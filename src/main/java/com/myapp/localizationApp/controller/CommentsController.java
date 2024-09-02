package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.CommentsDto;
import com.myapp.localizationApp.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    @PostMapping
    public ResponseEntity<CommentsDto> createComment(@RequestBody CommentsDto commentsDto) {
        CommentsDto createdComment = commentsService.createComment(commentsDto);
        return  new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentsDto> updateComment(@PathVariable Long id, @RequestBody CommentsDto commentsDto){
        CommentsDto updatedComment = commentsService.updateComment(id, commentsDto);
        return ResponseEntity.ok((updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletedComment(@PathVariable Long id) {
        commentsService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/term/{termId}")
    public ResponseEntity<List<CommentsDto>> getCommentsByTermId (@PathVariable Long termId) {
        List<CommentsDto> commentList = commentsService.findCommentsByTermId(termId);
        return ResponseEntity.ok(commentList);
    }

    @GetMapping("/term/{termId}/user/{userId}")
    public ResponseEntity<List<CommentsDto>> getCommentsByTermIdAndUserId (@PathVariable Long termId, @PathVariable Long userId) {
        List<CommentsDto> commentList = commentsService.findCommentByTermIdAndUserId(termId, userId);
        return ResponseEntity.ok(commentList);
    }
}
