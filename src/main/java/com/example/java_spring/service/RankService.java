package com.example.java_spring.service;

import com.example.java_spring.exception.UnauthorizedException;
import com.example.java_spring.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RankService {
    private UserRepository userRepository;

    public RankService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 랭킹리스트 불러오기
    @Transactional
    public List<Map<String, Object>> getRankList(Integer userId){
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<Object[]> results = entityManager.createQuery(
                "SELECT u.id as userId, u.nickname, ui.image_url as userImage, up.accumulate_point as accuPoint, " +
                        "(SELECT COUNT(id) FROM Post p WHERE p.user_id = u.id AND p.deletedAt IS NULL) as storyCount " +
                        "FROM User u " +
                        "LEFT JOIN u.userImages ui " +
                        "LEFT JOIN u.points up " +
                        "WHERE u.deletedAt IS NULL " +
                        "ORDER BY up.accumulate_point DESC, storyCount DESC, u.createdAt DESC ")
                .setMaxResults(10)
                .getResultList();

        List<Map<String, Object>> objectList = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("userId", result[0]);
            objectMap.put("nickname", result[1]);
            objectMap.put("userImage", result[2]);
            objectMap.put("accuPoint", result[3]);
            objectMap.put("storyCount", result[4]);

            // 다른 필드들에 대한 매핑 계속 추가
            objectList.add(objectMap);
        }

        return objectList;
    }
}
