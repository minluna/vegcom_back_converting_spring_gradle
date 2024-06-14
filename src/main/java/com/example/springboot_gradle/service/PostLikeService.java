package com.example.springboot_gradle.service;

import com.example.springboot_gradle.exception.NotFoundException;
import com.example.springboot_gradle.exception.UnauthorizedException;
import com.example.springboot_gradle.jpa.entity.PostLike;
import com.example.springboot_gradle.jpa.repository.PostLikeRepository;
import com.example.springboot_gradle.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class PostLikeService {
    private PostLikeRepository postLikeRepository;
    private UserRepository userRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 좋아요 누르기
    @Transactional
    public void postLike(Integer userId, Integer targetPostId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        postLikeRepository.save(PostLike.createLike(targetPostId, userId));
    }

    // 좋아요 여부 확인 및 좋아요 누적수 불러오기
    @Transactional
    public Map<String, Object> getlike(Integer userId, Integer targetPostId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        boolean checkLike = postLikeRepository.existsByPost_idAndUser_id(targetPostId, userId);
        Long likeCount = (Long) entityManager.createQuery(
                        "SELECT COUNT(pl.id) as likeCount " +
                                "FROM PostLike pl " +
                                "WHERE pl.post_id = :postId")
                .setParameter("postId", targetPostId)
                .getSingleResult();

        Map<String, Object> map = new HashMap<>();
        map.put("checkLike", checkLike);
        map.put("likeCount", likeCount);

        return map;
    }

    // 좋아요 취소하기
    @Transactional
    public void delLike(Integer userId, Integer targetPostId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        boolean existLike = postLikeRepository.existsByPost_idAndUser_id(targetPostId, userId);

        if (!existLike) {
            throw new NotFoundException("해당 게시불에는 사용자의 좋아요가 없습니다.");
        }

        entityManager.createQuery("DELETE FROM PostLike pl WHERE pl.user_id = :userId AND pl.post_id = :postId")
                .setParameter("userId", userId)
                .setParameter("postId", targetPostId)
                .executeUpdate();
    }
}
