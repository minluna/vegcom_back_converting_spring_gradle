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
public class CreateUser {
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 10, message = "비밀번호는 최소 10자리 이상 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력하세요.")
    private String nickname;

    @NotBlank(message = "imageUrl을 확인하세요.")
    private String image_url;
}
