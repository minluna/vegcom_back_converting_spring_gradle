package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {
    boolean existsByIdAndDeletedAtIsNull(Integer commentId);
    PostComment findByIdAndDeletedAtIsNullAndPost_id(Integer commentId, Integer postId);
    PostComment findByIdAndDeletedAtIsNull(Integer commentId);
}
