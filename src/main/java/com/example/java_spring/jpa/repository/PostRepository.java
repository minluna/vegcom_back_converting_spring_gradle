package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByIdAndDeletedAtIsNull(Integer postId);
    Post findByIdAndDeletedAtIsNull(Integer postId);
}
