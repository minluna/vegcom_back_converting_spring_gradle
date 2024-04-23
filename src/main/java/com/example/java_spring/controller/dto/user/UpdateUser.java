package com.example.java_spring.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {
    @NotBlank(message = "설명을 입력하세요.")
    private String description;

    @NotBlank(message = "이미지 파일은 필수 입력 사항입니다.")
    private String userImage;
}
