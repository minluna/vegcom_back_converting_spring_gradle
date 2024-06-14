package com.example.springboot_gradle.jpa.repository;

import com.example.springboot_gradle.jpa.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_idAndUser_id(Integer PostId, Integer UserId);
}
