package com.example.springboot_gradle.controller.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class JwtUser {
    private Integer userId;
    private String subject;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
}
