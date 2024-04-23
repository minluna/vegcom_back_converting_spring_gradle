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
@Table(name = "post_like")
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "like_id")
    @Comment("좋아요의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("좋아요를 누른 게시물의 ID")
    private Integer post_id;

    @Column(nullable = false)
    @Comment("좋아요를 누른 유저의 ID")
    private Integer user_id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // 외래키 설정
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", insertable = false, updatable = false)
    private Post post;

    // 좋아요 생성을 위해 만든 메서드
    public static PostLike createLike(Integer post_id, Integer user_id) {
        PostLike postLike = new PostLike();
        postLike.setPost_id(post_id);
        postLike.setUser_id(user_id);

        return postLike;
    }
}
