package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    PostImage findOneByPost_id(Integer postId);
}
