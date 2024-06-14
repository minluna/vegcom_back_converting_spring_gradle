package com.example.springboot_gradle.controller.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPost {
    private Integer postId;
    private Integer userId;
    private String nickname;
    private String content;
    private String postImageUrl;
}
