package com.example.springboot_gradle.service;

import com.example.springboot_gradle.exception.UnauthorizedException;
import com.example.springboot_gradle.jpa.repository.PostRepository;
import com.example.springboot_gradle.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private UserRepository userRepository;
    private PostRepository postRepository;

    public SearchService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 키워드로 게시물 불러오기
    @Transactional
    public List<Map<String, Object>> getKetwordPost(Integer userId, String keyword, Integer cursor) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<Object[]> results = new ArrayList<>();
        if (cursor == 0) {
            results = entityManager.createQuery(
                            "SELECT p.id as postId, p.user_id as userId, u.nickname as nickname, p.content as content, pi.image_url as postImageUrl " +
                                    "FROM Post p " +
                                    "LEFT JOIN p.postImages pi " +
                                    "LEFT JOIN p.user u " +
                                    "WHERE p.deletedAt IS NULL AND u.deletedAt IS NULL AND p.content LIKE :keyword " +
                                    "ORDER BY p.createdAt DESC"
                    )
                    .setParameter("keyword", "%" + keyword + "%")
                    .setMaxResults(5)
                    .getResultList();
        }
        else if (cursor == -1) {
            results.add(new Object[]{"검색어가 포함된 게시물 조회가 끝났습니다."});
        } else {
            results = entityManager.createQuery(
                            "SELECT p.id as postId, p.user_id as userId, u.nickname as nickname, p.content as content, pi.image_url as postImageUrl " +
                                    "FROM Post p " +
                                    "LEFT JOIN p.postImages pi " +
                                    "LEFT JOIN p.user u " +
                                    "WHERE p.deletedAt IS NULL AND u.deletedAt IS NULL AND p.content LIKE :keyword AND p.id < :cursor " +
                                    "ORDER BY p.createdAt DESC"
                    )
                    .setParameter("keyword", "%" + keyword + "%")
                    .setParameter("cursor", cursor)
                    .setMaxResults(5)
                    .getResultList();
        }

        List<Map<String, Object>> objectList = new ArrayList<>();
        if (cursor == -1) {
            Map<String, Object> endMessage = new HashMap<>();
            endMessage.put("endMessage", results.get(0));
            objectList.add(endMessage);
        } else {
            for (Object[] result : results) {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("postId", result[0]);
                objectMap.put("userId", result[1]);
                objectMap.put("nickname", result[2]);
                objectMap.put("content", result[3]);
                objectMap.put("postImage", result[4]);
                // 다른 필드들에 대한 매핑 계속 추가
                objectList.add(objectMap);
            }
        }
        return objectList;
    }
}
