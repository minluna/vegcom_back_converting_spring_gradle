package com.example.springboot_gradle.config;

import com.example.springboot_gradle.config.jwt.JwtTokenProvider;
import com.example.springboot_gradle.controller.dto.Result;
import com.example.springboot_gradle.controller.dto.jwt.JwtToken;
import com.example.springboot_gradle.jpa.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("oauth2@kakao.com", "oauth2loginpassword");
        Authentication authenticationCheck = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authenticationCheck);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", ((User) authenticationCheck.getPrincipal()).getId());
        map.put("token", jwtToken.getAccessToken());

        // JWT 토큰을 클라이언트에게 전달하기 위해 응답에 추가
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(convertObjectToJson(Result.res(HttpStatus.OK.toString(), "로그인이 성공적으로 완료되었습니다.", map)));

        // response.setStatus(200);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
