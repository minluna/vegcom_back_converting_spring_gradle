package com.example.springboot_gradle.jpa.entity;

import com.example.springboot_gradle.controller.dto.comment.CreateComment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)  // Entity를 DB에 적용하기 이전, 이후에 커스텀 콜백을 적용하기 위한 클래스
// @DynamicInsert  // insert 시 null값인 컬럼은 insert문에서 제외
// @DynamicUpdate  // update 시 전체 update가 아닌 변경된 데이터만 update
@Table(name = "post_comment")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "comment_id")
    @Comment("댓글의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("댓글 작성자의 ID")
    private Integer user_id;

    @Column(nullable = false)
    @Comment("댓글이 달린 게시물의 ID")
    private Integer post_id;

    @Column(nullable = false)
    @Comment("댓글의 내용")
    private String content;

    @CreatedDate    // 생성일자 자동 입력
    @Column(nullable = false, updatable = false)    // 생성일자는 수정 불가능하게 설정
    @Comment("댓글 생성일자")
    private LocalDateTime createdAt;

    @LastModifiedDate   // 수정일자 자동 입력
    @Column(nullable = false)
    @Comment("댓글 수정일자")
    private LocalDateTime updatedAt;

    @Column
    @Comment("댓글 삭제일자")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @ColumnDefault("'0'")  // 컬럼 기본값 지정
    @Comment("댓글의 부모 ID")
    private Integer parent_id;

    // 삭제
    public void deleteSoftly(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // 확인
    public boolean isSoftDeleted() {
        return null != deletedAt;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // 외래키 설정
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", insertable = false, updatable = false)
    private Post post;

    // 게시물 생성을 위해 만든 메서드
    public static PostComment createComment(Integer user_id, CreateComment createComment) {
        PostComment postComment = new PostComment();
        postComment.setUser_id(user_id);
        postComment.setPost_id(createComment.getPost_id());
        postComment.setContent(createComment.getContent());
        postComment.setParent_id(createComment.getParentId());

        return postComment;
    }
}
