package com.example.springboot_gradle.jpa.repository;

import com.example.springboot_gradle.jpa.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    PostImage findOneByPost_id(Integer postId);
}
