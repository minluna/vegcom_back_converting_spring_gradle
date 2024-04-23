package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findById(Integer id);
    List<User> findAllByDeletedAtIsNull();
    boolean existsByIdAndDeletedAtIsNull(Integer id);
    User findByIdAndDeletedAtIsNull(Integer id);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findByNickname(String nickname);
}
