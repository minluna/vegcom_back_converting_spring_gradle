package com.example.springboot_gradle.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "post_image")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "image_id")
    @Comment("게시물 이미지의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("이미지를 설정한 게시물의 ID")
    private Integer post_id;

    @Column(nullable = false)
    @Comment("게시물 이미지의 주소")
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", insertable = false, updatable = false)  // 외래키 설정
    private Post post;

    // 게시물 생성을 위해 만든 메서드
    public static PostImage createPost(Integer post_id, String image_url) {
        PostImage postImage = new PostImage();
        postImage.setPost_id(post_id);
        postImage.setImage_url(image_url);

        return postImage;
    }
}
