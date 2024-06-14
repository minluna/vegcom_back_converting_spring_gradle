package com.example.springboot_gradle.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {
    @NotBlank(message = "설명을 입력하세요.")
    private String description;

    @NotNull(message = "이미지 파일은 필수 입력 사항입니다.")
    private MultipartFile userImage;
}
