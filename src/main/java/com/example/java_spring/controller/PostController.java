package com.example.java_spring.controller;

import com.example.java_spring.controller.dto.post.CreatePost;
import com.example.java_spring.controller.dto.jwt.JwtUser;
import com.example.java_spring.controller.dto.Result;
import com.example.java_spring.controller.dto.post.UpdatePost;
import com.example.java_spring.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 전체 피드 불러오기(시간순)
    @GetMapping("/list/{cursor}")
    public ResponseEntity<Object> findAllPost(Authentication authentication, @PathVariable(name = "cursor") Integer cursor) {
        List<Map<String, Object>> allPost = postService.getAllPost(((JwtUser) authentication.getPrincipal()).getUserId(), cursor);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시물 전체 조회를 성공했습니다.",
                        allPost));
    }

    // 피드 개수, 피드 작성자 수 불러오기
    @GetMapping("/count")
    public ResponseEntity<Object> findCount(Authentication authentication) {
        Map<String, Long> count = postService.getCount(((JwtUser) authentication.getPrincipal()).getUserId());

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "피드 수와 피드를 작성한 유저 수 불러오기에 성공했습니다.",
                        count));
    }

    // 특정 유저의 피드 찾기
    @GetMapping("/mypage/{targetId}")
    public ResponseEntity<Object> findPostByUser(Authentication authentication, @PathVariable(name = "targetId") Integer targetId) {
        List<Map<String, Object>> postList = postService.getPostByUser(((JwtUser) authentication.getPrincipal()).getUserId(), targetId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저가 작성한 피드 정보 불러오기에 성공했습니다.",
                        postList));
    }

    // 특정 유저가 좋아요한 피드 찾기
    @GetMapping("/like/{targetId}")
    public ResponseEntity<Object> findUserLikePost(Authentication authentication, @PathVariable(name = "targetId") Integer targetId) {
        List<Map<String, Object>> postList = postService.getUserLikePost(((JwtUser) authentication.getPrincipal()).getUserId(), targetId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저가 좋아요한 피드 정보 불러오기에 성공했습니다.",
                        postList));
    }

    // 피드 작성
    @PostMapping("/")
    public ResponseEntity<Object> createPost(Authentication authentication, @Valid @RequestBody CreatePost createPost) {
        postService.postPost(((JwtUser) authentication.getPrincipal()).getUserId(), createPost);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시물 작성을 성공했습니다."));
    }

    // 피드 상세페이지
    @GetMapping("/{targetPostId}")
    public ResponseEntity<Object> findPost(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId) {
        Map<String, Object> post = postService.getPost(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시물 상세 조회를 성공했습니다.",
                        post));
    }

    // 피드 수정
    @PutMapping("/{targetPostId}")
    public ResponseEntity<Object> updatePost(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId, @Valid @RequestBody UpdatePost updatePost) {
        postService.setPost(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId, updatePost);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시물 수정을 성공했습니다."));
    }

    // 피드 삭제
    @DeleteMapping("/{targetPostId}")
    public ResponseEntity<Object> deletePost(Authentication authentication, @PathVariable(name = "targetPostId") Integer targetPostId) {
        postService.delPost(((JwtUser) authentication.getPrincipal()).getUserId(), targetPostId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "게시물 삭제를 성공했습니다."));
    }
}
