package com.example.java_spring.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

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
}
