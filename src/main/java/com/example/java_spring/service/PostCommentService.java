package com.example.java_spring.service;

import com.example.java_spring.controller.dto.comment.CreateComment;
import com.example.java_spring.controller.dto.comment.UpdateComment;
import com.example.java_spring.exception.NotFoundException;
import com.example.java_spring.exception.UnauthorizedException;
import com.example.java_spring.jpa.entity.PostComment;
import com.example.java_spring.jpa.repository.CommentRepository;
import com.example.java_spring.jpa.repository.PostRepository;
import com.example.java_spring.jpa.repository.UserRepository;
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
public class PostCommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;

    public PostCommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 댓글 생성하기
    @Transactional
    public void postComment(Integer userId, CreateComment createComment){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        commentRepository.save(PostComment.createComment(userId, createComment));
    }

    // 댓글 불러오기
    @Transactional
    public Map<String, Object> getComment(Integer userId, Integer targetPostId, Integer cursor){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        boolean existPost = postRepository.existsByIdAndDeletedAtIsNull(targetPostId);

        if (!existPost) {
            throw new NotFoundException("요청한 게시물의 정보를 찾을 수 없습니다.");
        }

        List<Object[]> CommentListZero = new ArrayList<>();
        List<Object[]> CommentListOther = new ArrayList<>();
        if (cursor == 0) {
            CommentListZero = entityManager.createQuery(
                    "SELECT c.id as commentId, c.user_id as userId, u.nickname, ui.image_url as UserImage, c.content, c.parent_id, c.createdAt " +
                            "FROM PostComment c " +
                            "LEFT JOIN c.user u " +
                            "LEFT JOIN u.userImages ui " +
                            "WHERE c.deletedAt IS NULL AND u.deletedAt IS NULL AND c.parent_id = 0 AND c.post_id = :post_id " +
                            "ORDER BY c.createdAt DESC")
                    .setParameter("post_id", targetPostId)
                    .setMaxResults(10)
                    .getResultList();

            CommentListOther = entityManager.createQuery(
                    "SELECT c.id as commentId, c.user_id as userId, u.nickname, ui.image_url as UserImage, c.content, c.parent_id, c.createdAt " +
                            "FROM PostComment c " +
                            "LEFT JOIN c.user u " +
                            "LEFT JOIN u.userImages ui " +
                            "WHERE c.deletedAt IS NULL AND u.deletedAt IS NULL AND c.parent_id != 0 AND c.post_id = :post_id " +
                            "ORDER BY c.createdAt DESC")
                    .setParameter("post_id", targetPostId)
                    .getResultList();
        } else if (cursor == -1) {
            CommentListZero.add(new Object[]{"전체 댓글 조회가 끝났습니다."});
            CommentListOther.add(new Object[]{"전체 댓글 조회가 끝났습니다."});
        } else {
            CommentListZero = entityManager.createQuery(
                            "SELECT c.id as commentId, c.user_id as userId, u.nickname, ui.image_url as UserImage, c.content, c.parent_id, c.createdAt " +
                                    "FROM PostComment c " +
                                    "LEFT JOIN c.user u " +
                                    "LEFT JOIN u.userImages ui " +
                                    "WHERE c.deletedAt IS NULL AND u.deletedAt IS NULL AND c.parent_id = 0 AND c.post_id = :post_id AND c.id < :cursor " +
                                    "ORDER BY c.createdAt DESC")
                    .setParameter("post_id", targetPostId)
                    .setParameter("cursor", cursor)
                    .setMaxResults(10)
                    .getResultList();

            CommentListOther = entityManager.createQuery(
                            "SELECT c.id as commentId, c.user_id as userId, u.nickname, ui.image_url as UserImage, c.content, c.parent_id, c.createdAt " +
                                    "FROM PostComment c " +
                                    "LEFT JOIN c.user u " +
                                    "LEFT JOIN u.userImages ui " +
                                    "WHERE c.deletedAt IS NULL AND u.deletedAt IS NULL AND c.parent_id != 0 AND c.post_id = :post_id " +
                                    "ORDER BY c.createdAt DESC")
                    .setParameter("post_id", targetPostId)
                    .getResultList();
        }

        List<Map<String, Object>> objectListZero = new ArrayList<>();
        List<Map<String, Object>> objectListOther = new ArrayList<>();
        if (cursor == -1) {
            Map<String, Object> endMessageZero = new HashMap<>();
            Map<String, Object> endMessageOther = new HashMap<>();
            endMessageZero.put("endMessage", CommentListZero.get(0));
            endMessageOther.put("endMessage", CommentListOther.get(0));
            objectListZero.add(endMessageZero);
            objectListOther.add(endMessageOther);
        } else {
            for (Object[] resultZero : CommentListZero) {
                Map<String, Object> objectMapZero = new HashMap<>();
                objectMapZero.put("commentId", resultZero[0]);
                objectMapZero.put("userId", resultZero[1]);
                objectMapZero.put("nickname", resultZero[2]);
                objectMapZero.put("userImage", resultZero[3]);
                objectMapZero.put("content", resultZero[4]);
                objectMapZero.put("parentId", resultZero[5]);
                objectMapZero.put("createdAt", resultZero[6]);
                // 다른 필드들에 대한 매핑 계속 추가
                objectListZero.add(objectMapZero);
            }

            for (Object[] resultOther : CommentListOther) {
                Map<String, Object> objectMapOther = new HashMap<>();
                objectMapOther.put("commentId", resultOther[0]);
                objectMapOther.put("userId", resultOther[1]);
                objectMapOther.put("nickname", resultOther[2]);
                objectMapOther.put("userImage", resultOther[3]);
                objectMapOther.put("content", resultOther[4]);
                objectMapOther.put("parentId", resultOther[5]);
                objectMapOther.put("createdAt", resultOther[6]);
                // 다른 필드들에 대한 매핑 계속 추가
                objectListOther.add(objectMapOther);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("commentListZero", objectListZero);
        result.put("commentListOther", objectListOther);

        return result;
    }

    // 댓글 수정하기
    public void setComment(Integer userId, Integer targetCommentId, UpdateComment updateComment){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        Integer targetPostId = updateComment.getPost_id();
        PostComment targetComment = commentRepository.findByIdAndDeletedAtIsNullAndPost_id(targetCommentId, targetPostId);

        if (targetComment == null) {
            throw new NotFoundException("요청한 댓글의 정보를 찾을 수 없습니다.");
        }

        String updateContent = updateComment.getContent();

        targetComment.setContent(updateContent);
        commentRepository.save(targetComment);
    }

    // 댓글 삭제하기
    public void delComment(Integer userId, Integer targetCommentId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        PostComment targetComment = commentRepository.findByIdAndDeletedAtIsNull(targetCommentId);

        // if (targetComment == null) {
        //     throw new NotFoundException("요청한 댓글의 정보를 찾을 수 없습니다.");
        // }

        targetComment.deleteSoftly(LocalDateTime.now());

        if (targetComment.isSoftDeleted()) {
            commentRepository.save(targetComment);
        }
    }
}
