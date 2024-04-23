package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_idAndUser_id(Integer PostId, Integer UserId);
}
