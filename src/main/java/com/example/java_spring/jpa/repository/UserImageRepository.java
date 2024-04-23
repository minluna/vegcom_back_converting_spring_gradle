package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    UserImage findOneByUser_id(Integer userId);
}
