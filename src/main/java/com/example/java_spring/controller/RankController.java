package com.example.java_spring.controller;

import com.example.java_spring.controller.dto.jwt.JwtUser;
import com.example.java_spring.controller.dto.Result;
import com.example.java_spring.service.RankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rank")
public class RankController {
    private RankService rankService;

    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    // 랭킹리스트 불러오기
    @GetMapping("/list")
    public ResponseEntity<Object> findRankList(Authentication authentication){
        List<Map<String, Object>> getRank = rankService.getRankList(((JwtUser) authentication.getPrincipal()).getUserId());

        return ResponseEntity.ok(
                Result.res(HttpStatus.OK.toString(),
                        "Top10 랭킹 리스트 불러오기에 성공했습니다.",
                        getRank));
    }
}
