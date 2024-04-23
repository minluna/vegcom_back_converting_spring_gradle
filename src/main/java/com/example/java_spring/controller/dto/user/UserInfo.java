package com.example.java_spring.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Integer userId;
    private String email;
    private String nickname;
    private String description;
    private LocalDateTime createdAt;
    private String userImage;
    private Integer accuPoint;
    private Long storyCount;
}
