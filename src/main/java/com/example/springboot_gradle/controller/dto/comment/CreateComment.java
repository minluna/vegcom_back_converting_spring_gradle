package com.example.springboot_gradle.controller.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateComment {
    @Positive(message = "게시물 ID를 확인해주세요")
    private Integer post_id;

    @NotBlank(message = "게시물 내용을 입력하세요.")
    @Size(max = 200, message = "게시물 내용은 최대 200글자까지 허용됩니다.")
    private String content;

    @PositiveOrZero(message = "게시물 부모 ID를 확인해주세요.")
    private Integer parentId;
}
