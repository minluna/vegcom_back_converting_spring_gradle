package com.example.springboot_gradle.service;

import com.example.springboot_gradle.controller.dto.post.CreatePost;
import com.example.springboot_gradle.controller.dto.post.UpdatePost;
import com.example.springboot_gradle.exception.NotFoundException;
import com.example.springboot_gradle.exception.UnauthorizedException;
import com.example.springboot_gradle.jpa.entity.Post;
import com.example.springboot_gradle.jpa.entity.PostImage;
import com.example.springboot_gradle.jpa.repository.PostImageRepository;
import com.example.springboot_gradle.jpa.repository.PostRepository;
import com.example.springboot_gradle.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    private PostRepository postRepository;
    private PostImageRepository postImageRepository;
    private UserRepository userRepository;

    public PostService(PostRepository postRepository, PostImageRepository postImageRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.userRepository = userRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 전체 피드 불러오기(시간순)
    @Transactional
    public List<Map<String, Object>> getAllPost(Integer userId, Integer cursor) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<Object[]> results = new ArrayList<>();
        if (cursor == 0) {
            results = entityManager.createQuery(
                            "SELECT p.id as postId, p.user_id as userId, u.nickname as nickname, p.content as content, pi.image_url as postImageUrl, ui.image_url as userImageUrl, p.createdAt as createdAt " +
                                    "FROM Post p " +
                                    "LEFT JOIN p.postImages pi " +
                                    "LEFT JOIN p.user u " +
                                    "LEFT JOIN p.user.userImages ui " +
                                    "WHERE p.deletedAt IS NULL AND u.deletedAt IS NULL " +
                                    "ORDER BY p.createdAt DESC"
                    )
                    .setMaxResults(5)
                    .getResultList();
        } else if (cursor == -1) {
            results.add(new Object[]{"전체 게시물 조회가 끝났습니다."});
        } else {
            results = entityManager.createQuery(
                            "SELECT p.id as postId, p.user_id as userId, u.nickname as nickname, p.content as content, pi.image_url as postImageUrl, ui.image_url as userImageUrl, p.createdAt as createdAt " +
                                    "FROM Post p " +
                                    "LEFT JOIN p.postImages pi " +
                                    "LEFT JOIN p.user u " +
                                    "LEFT JOIN p.user.userImages ui " +
                                    "WHERE p.deletedAt IS NULL AND u.deletedAt IS NULL AND p.id < :cursor " +
                                    "ORDER BY p.createdAt DESC"
                    )
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
                objectMap.put("userImage", result[5]);
                objectMap.put("createdAt", result[6]);
                // 다른 필드들에 대한 매핑 계속 추가
                objectList.add(objectMap);
            }
        }

        return objectList;
        }

    // 피드 개수, 피드 작성자 수 불러오기
    @Transactional
    public Map<String, Long> getCount(Integer userId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        Object[] count = (Object[]) entityManager.createQuery(
                "SELECT COUNT(p.id) as postCount, COUNT(DISTINCT p.user.id) as userCount " +
                        "FROM Post p " +
                        "WHERE FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m-%d') = CURRENT_DATE")
                .getSingleResult();

        Map<String, Long> map = new HashMap<>();
        map.put("postCount", (Long) count[0]);
        map.put("userCount", (Long) count[1]);

        return map;
    }

    // 특정 유저의 피드 찾기
    @Transactional
    public List<Map<String, Object>> getPostByUser(Integer userId, Integer targetId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<Object[]> results = entityManager.createQuery(
                        "SELECT p.id as postId, pi.image_url as postImageUrl " +
                                "FROM Post p " +
                                "LEFT JOIN p.postImages pi " +
                                "WHERE p.deletedAt IS NULL AND p.user_id = :user_id " +
                                "ORDER BY p.createdAt DESC"
                )
                .setParameter("user_id", targetId)
                .getResultList();

        List<Map<String, Object>> objectList = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("postId", result[0]);
            objectMap.put("imageUrl", result[1]);

            // 다른 필드들에 대한 매핑 계속 추가
            objectList.add(objectMap);
        }

        return objectList;
    }

    // 특정 유저가 좋아요한 피드 찾기
    @Transactional
    public List<Map<String, Object>> getUserLikePost(Integer userId, Integer targetId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<Object[]> results = entityManager.createQuery(
                        "SELECT p.id as postId, pi.image_url as postImageUrl " +
                                "FROM Post p " +
                                "LEFT JOIN p.postImages pi " +
                                "WHERE p.deletedAt IS NULL AND post.id IN (SELECT post_id FROM PostLike where user_id = :likeUserId) " +
                                "ORDER BY p.createdAt DESC"
                )
                .setParameter("likeUserId", targetId)
                .getResultList();

        List<Map<String, Object>> objectList = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("postId", result[0]);
            objectMap.put("imageUrl", result[1]);

            // 다른 필드들에 대한 매핑 계속 추가
            objectList.add(objectMap);
        }

        return objectList;
    }

    // 피드 작성
    @Transactional
    public void postPost(Integer userId, CreatePost createPost) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        Post createdPost = postRepository.save(Post.createPost(userId, createPost.getContent()));

        postImageRepository.save(PostImage.createPost(createdPost.getId(), createPost.getPostImage()));

        entityManager.createQuery(
                "UPDATE Point p " +
                        "SET p.current_point = p.current_point + 1000, p.accumulate_point = p.accumulate_point + 1000 " +
                        "WHERE p.user_id = :user_id AND " +
                        "      3 >= (SELECT COUNT(p.id) FROM Post p WHERE p.user_id = :user_id AND FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m-%d') = FUNCTION('CURDATE'))"
        )
                .setParameter("user_id", userId)
                .executeUpdate();
    }

    // 피드 상세페이지
    @Transactional
    public Map<String, Object> getPost(Integer userId, Integer targetPostId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        boolean existPost = postRepository.existsByIdAndDeletedAtIsNull(targetPostId);

        if (!existPost) {
            throw new NotFoundException("요청한 게시물의 정보를 찾을 수 없습니다.");
        }

        Object[] post = (Object[]) entityManager.createQuery(
                        "SELECT p.id as postId, p.user_id as userId, u.nickname, p.content, pi.image_url as PostImage, ui.image_url as UserImage, p.createdAt " +
                                "FROM Post p " +
                                "LEFT JOIN p.postImages pi " +
                                "LEFT JOIN p.user u " +
                                "LEFT JOIN u.userImages ui " +
                                "WHERE p.deletedAt IS NULL AND u.deletedAt IS NULL AND p.id = :postId ")
                .setParameter("postId", targetPostId)
                .getSingleResult();

        Map<String, Object> map = new HashMap<>();
        map.put("postId", post[0]);
        map.put("userId", post[1]);
        map.put("nickname", post[2]);
        map.put("content", post[3]);
        map.put("postImage", post[4]);
        map.put("userImage", post[5]);
        map.put("createdAt", post[6]);

        return map;
    }

    // 피드 수정
    @Transactional
    public void setPost(Integer userId, Integer targetPostId, UpdatePost updatePost) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        Post targetPost = postRepository.findByIdAndDeletedAtIsNull(targetPostId);
        PostImage targetPostImage = postImageRepository.findOneByPost_id(targetPostId);

        if (targetPost == null || targetPostImage == null) {
            throw new NotFoundException("요청한 게시물의 정보를 찾을 수 없습니다.");
        }

        String updateContent = updatePost.getContent();
        String updatePostImage = updatePost.getPostImage();

        targetPost.setContent((updateContent));
        postRepository.save(targetPost);

        targetPostImage.setImage_url(updatePostImage);
        postImageRepository.save(targetPostImage);
    }

    // 피드 삭제
    @Transactional
    public void delPost(Integer userId, Integer targetPostId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        Post targetPost = postRepository.findByIdAndDeletedAtIsNull(targetPostId);

        if (targetPost == null) {
            throw new NotFoundException("요청한 게시물의 정보를 찾을 수 없습니다.");
        }

        targetPost.deleteSoftly(LocalDateTime.now());

        if (targetPost.isSoftDeleted()) {
            postRepository.save(targetPost);
        }
    }
}
