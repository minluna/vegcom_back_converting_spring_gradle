package com.example.springboot_gradle.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_image")
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "image_id")
    @Comment("이미지의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("이미지를 설정한 유저의 ID")
    private Integer user_id;

    @Column(nullable = false)
    @Comment("유저 이미지의 주소")
    private String image_url;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // 외래키 설정
    private User user;

    public static UserImage registerUser(Integer user_id, String image_url) {
        UserImage userImage = new UserImage();
        userImage.setUser_id(user_id);
        userImage.setImage_url(image_url);

        return userImage;
    }

    public static String saveUserImage(MultipartFile file) {
        // 파일 저장 로직을 추가합니다.
        // 파일을 저장하고 파일 경로를 반환합니다.

        String fileName = file.getOriginalFilename();
        String uploadDir = "user-images/";

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            return "1";
        }
    }
}
