package com.example.java_spring.controller;

import com.example.java_spring.controller.dto.*;
import com.example.java_spring.controller.dto.jwt.JwtUser;
import com.example.java_spring.controller.dto.user.CreateUser;
import com.example.java_spring.controller.dto.user.LoginUser;
import com.example.java_spring.controller.dto.user.UpdateUser;
import com.example.java_spring.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody CreateUser createUser) {
        userService.saveUser(createUser);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "회원가입을 성공적으로 완료되었습니다."));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Object> signIn(@Valid @RequestBody LoginUser loginUser) {
        Map<String, Object> loginInfo = userService.login(loginUser);
        // log.info("request username = {}, password = {}", loginUser.getEmail(), loginUser.getPassword());
        // log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "로그인이 성공적으로 완료되었습니다.",
                        loginInfo)
        );
    }

    // 로그인 검증

    // 전체 유저 수
    @GetMapping("/userCount")
    public ResponseEntity<Object> allUserCount(Authentication authentication) {
        Integer userAllCount = userService.getAllCount(((JwtUser) authentication.getPrincipal()).getUserId());

        // JSON 응답 객체 생성
        // ResponseEntity로 응답 반환
        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "전체 유저 수 불러오기가 성공적으로 완료되었습니다.",
                        userAllCount));

    }

    // 유저의 누적 포인트
    @GetMapping("/point")
    public ResponseEntity<Object> checkPoint(Authentication authentication) {
        Map<String, Object> userPoint = userService.getPoint(((JwtUser) authentication.getPrincipal()).getUserId());

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저 포인트 내역 불러오기가 성공적으로 완료되었습니다.",
                        userPoint));
    }

    // 유저 정보 찾기
    @GetMapping("/{targetId}")
    public ResponseEntity<Object> findUser(Authentication authentication, @PathVariable(name = "targetId") Integer targetId) {
        Map<String, Object> userInfo = userService.getUserInfo(((JwtUser) authentication.getPrincipal()).getUserId(), targetId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저 정보 불러오기가 성공적으로 완료되었습니다.",
                        userInfo));
    }

    // 유저 정보 수정
    @PutMapping("/{targetId}")
    public ResponseEntity<Object> updateUser(Authentication authentication, @PathVariable(name = "targetId") Integer targetId, @RequestParam("description") String description,
                                             @RequestParam("userImage") MultipartFile userImage) {
        log.warn("UserImage: " + userImage.getOriginalFilename());
        userService.setUser(((JwtUser) authentication.getPrincipal()).getUserId(), targetId, description, userImage);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저 정보 수정하기가 성공적으로 완료되었습니다.",
                        description));
    }

    // 유저 정보 삭제
    @DeleteMapping("/{targetId}")
    public ResponseEntity<Object> deleteUser(Authentication authentication, @PathVariable(name = "targetId") Integer targetId) {
        userService.delUser(((JwtUser) authentication.getPrincipal()).getUserId(), targetId);

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "유저 정보 삭제하기가 성공적으로 완료되었습니다."));
    }
}
