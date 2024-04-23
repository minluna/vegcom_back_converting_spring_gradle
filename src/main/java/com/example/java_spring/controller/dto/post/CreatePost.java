package com.example.java_spring.controller.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePost {
    @NotBlank(message = "게시물 내용을 입력하세요.")
    @Size(max = 200, message = "게시물 내용은 최대 200글자까지 허용됩니다.")
    private String content;

    @NotBlank(message = "이미지 파일은 필수 입력 사항입니다.")
    private String postImage;
}
