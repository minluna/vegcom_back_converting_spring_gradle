package com.example.springboot_gradle.controller;

import com.example.springboot_gradle.controller.dto.jwt.JwtUser;
import com.example.springboot_gradle.controller.dto.Result;
import com.example.springboot_gradle.service.PostLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/like")
public class PostLikeController {
    private PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    // 좋아요 누르기
    @PostMapping("/{targetPostId}")
    public ResponseEntity<Object> createLike(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId) {
        postLikeService.postLike(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "좋아요 목록 생성에 성공하셨습니다."));
    }

    // 좋아요 여부 확인 및 좋아요 누적수 불러오기
    @GetMapping("/{targetPostId}")
    public ResponseEntity<Object> findLike(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId) {
        Map<String, Object> like = postLikeService.getlike(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "좋아요 여부 확인 및 좋아요 누적수 불러오기에 성공했습니다.",
                        like));
    }

    // 좋아요 취소하기
    @DeleteMapping("/{targetPostId}")
    public ResponseEntity<Object> deleteLike(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId) {
        postLikeService.delLike(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "좋아요 목록 삭제에 성공하였습니다."));
    }
}
