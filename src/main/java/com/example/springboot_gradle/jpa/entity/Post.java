package com.example.springboot_gradle.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)  // Entity를 DB에 적용하기 이전, 이후에 커스텀 콜백을 적용하기 위한 클래스
@DynamicInsert  // insert 시 null값인 컬럼은 insert문에서 제외
@DynamicUpdate  // update 시 전체 update가 아닌 변경된 데이터만 update
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("게시물의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @org.hibernate.annotations.Comment("게시물 작성자의 ID")
    private Integer user_id;

    @Column(nullable = false)
    @org.hibernate.annotations.Comment("게시물의 내용")
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")  // 컬럼 기본값 지정
    @org.hibernate.annotations.Comment("게시물 공개여부(0: 공개, 1: 비공개)")
    private Integer is_private;

    @CreatedDate    // 생성일자 자동 입력
    @Column(nullable = false, updatable = false)    // 생성일자는 수정 불가능하게 설정
    @org.hibernate.annotations.Comment("게시물 생성일자")
    private LocalDateTime createdAt;

    @LastModifiedDate   // 수정일자 자동 입력
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("게시물 수정일자")
    private LocalDateTime updatedAt;

    @Column
    @org.hibernate.annotations.Comment("게시물 삭제일자")
    private LocalDateTime deletedAt;

    // 삭제
    public void deleteSoftly(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // 확인
    public boolean isSoftDeleted() {
        return null != deletedAt;
    }

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postComments;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // 외래키 설정
    private User user;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> postImages;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikes;

    // 게시물 생성을 위해 만든 메서드
    public static Post createPost(Integer user_id, String content) {
        Post post = new Post();
        post.setUser_id(user_id);
        post.setContent(content);

        return post;
    }
}
