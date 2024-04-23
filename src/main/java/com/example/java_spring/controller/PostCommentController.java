package com.example.java_spring.controller;

import com.example.java_spring.controller.dto.*;
import com.example.java_spring.controller.dto.comment.CreateComment;
import com.example.java_spring.controller.dto.comment.UpdateComment;
import com.example.java_spring.controller.dto.jwt.JwtUser;
import com.example.java_spring.service.PostCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comment")
public class PostCommentController {
    private PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    // 댓글 생성하기
    @PostMapping("/")
    public ResponseEntity<Object> createComment(Authentication authentication, @Valid @RequestBody CreateComment createComment){
        postCommentService.postComment(((JwtUser) authentication.getPrincipal()).getUserId(), createComment);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "댓글 추가하기에 성공했습니다."));
    }

    // 댓글 불러오기
    @GetMapping("")
    public ResponseEntity<Object> findComment(Authentication authentication, @RequestParam Integer postId, Integer cursor){
        Map<String, Object> getComment = postCommentService.getComment(((JwtUser) authentication.getPrincipal()).getUserId(), postId, cursor);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시글 총 댓글 불러오기에 성공하셨습니다.",
                        getComment));
    }

    // 댓글 수정하기
    @PutMapping("/{targetCommentId}")
    public ResponseEntity<Object> updateComment(Authentication authentication, @PathVariable(name="targetCommentId") Integer targetCommentId, @Valid @RequestBody UpdateComment updateComment){
        postCommentService.setComment(((JwtUser) authentication.getPrincipal()).getUserId(), targetCommentId, updateComment);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "댓글 수정하기에 성공했습니다."));
    }

    // 댓글 삭제하기
    @DeleteMapping("/{targetCommentId}")
    public ResponseEntity<Object> deleteComment(Authentication authentication, @PathVariable(name="targetCommentId") Integer targetCommentId){
        postCommentService.delComment(((JwtUser) authentication.getPrincipal()).getUserId(), targetCommentId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "댓글 삭제하기에 성공했습니다."));
    }
}
