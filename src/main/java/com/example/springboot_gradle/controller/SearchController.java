package com.example.springboot_gradle.controller;

import com.example.springboot_gradle.controller.dto.jwt.JwtUser;
import com.example.springboot_gradle.controller.dto.Result;
import com.example.springboot_gradle.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // 키워드로 게시물 불러오기
    @GetMapping("/keyword")
    public ResponseEntity<Object> findKeywordPost(Authentication authentication, @RequestParam String keyword, Integer cursor){
        List<Map<String, Object>> searchPost = searchService.getKetwordPost(((JwtUser) authentication.getPrincipal()).getUserId(), keyword, cursor);

        // JSON 응답 객체 생성
        // ResponseEntity로 응답 반환
        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "키워드를 포함한 게시물 불러오기에 성공했습니다.",
                        searchPost));
    }
}
