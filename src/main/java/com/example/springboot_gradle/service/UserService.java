package com.example.springboot_gradle.service;

import com.example.springboot_gradle.config.jwt.JwtTokenProvider;
import com.example.springboot_gradle.controller.dto.jwt.JwtToken;
import com.example.springboot_gradle.controller.dto.user.CreateUser;
import com.example.springboot_gradle.controller.dto.user.LoginUser;
import com.example.springboot_gradle.controller.dto.user.UserInfo;
import com.example.springboot_gradle.exception.ConflictException;
import com.example.springboot_gradle.exception.NotFoundException;
import com.example.springboot_gradle.exception.UnauthorizedException;
import com.example.springboot_gradle.jpa.entity.Point;
import com.example.springboot_gradle.jpa.entity.User;
import com.example.springboot_gradle.jpa.entity.UserImage;
import com.example.springboot_gradle.jpa.repository.PointRepository;
import com.example.springboot_gradle.jpa.repository.UserImageRepository;
import com.example.springboot_gradle.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserImageRepository userImageRepository;
    private PointRepository pointRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private Integer lastProcessedId = 1;

    public UserService(UserRepository userRepository, UserImageRepository userImageRepository, PointRepository pointRepository, AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.pointRepository = pointRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 회원가입
    @Transactional
    public void saveUser(CreateUser createUser) {

        boolean existUser = userRepository.existsByEmail(createUser.getEmail());

        if (existUser) {
            throw new ConflictException("이 이메일은 현재 사용중입니다. 다른 이메일을 입력해 주세요.");
        }

        User createdUser = userRepository.save(User.registerUser(createUser));

        userImageRepository.save(UserImage.registerUser(createdUser.getId(), createUser.getImage_url()));

        pointRepository.save(Point.registerUser(createdUser.getId()));
    }

    // 로그인
    @Transactional
    public Map<String, Object> login(LoginUser loginUser) {
        // 1. email + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword());

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        User targetUser = userRepository.findByEmail(loginUser.getEmail());

        Map<String, Object> map = new HashMap<>();
        map.put("userId", targetUser.getId());
        map.put("token", jwtToken.getAccessToken());

        return map;
    }

    // 로그인 검증

    // 전체 유저 수
    @Transactional
    public Integer getAllCount(Integer userId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        List<User> AllUsers = userRepository.findAllByDeletedAtIsNull();

        return AllUsers.size();
    }

    // 유저의 누적 포인트
    @Transactional
    public Map<String, Object> getPoint(Integer userId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        User targetUser = userRepository.findById(userId);

        UserImage targetUserImage = userImageRepository.findOneByUser_id(targetUser.getId());

        if (targetUserImage == null) {
            throw new NotFoundException("요청한 사용자의 이미지를 찾을 수 없습니다.");
        }

        Point targetUserPoint = pointRepository.findOneById(targetUser.getId());

        if (targetUserPoint == null) {
            throw new NotFoundException("요청한 사용자의 포인트를 찾을 수 없습니다.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", targetUser.getId());
        map.put("nickname", targetUser.getNickname());
        map.put("imageUrl", targetUserImage.getImage_url());
        map.put("accuPoint", targetUserPoint.getAccumulate_point());
        return map;
    }

    // 유저 정보 찾기
    @Transactional
    public Map<String, Object> getUserInfo(Integer userId, Integer targetId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        User targetUser = userRepository.findById(targetId);
        if (targetUser == null) throw new NotFoundException("요청한 사용자의 정보를 찾을 수 없습니다.");

        UserInfo userInfo = entityManager.createQuery(
                        "SELECT NEW UserInfo(u.id, u.email, u.nickname, u.description, u.createdAt, ui.image_url, p.accumulate_point, " +
                                "(SELECT count(pst.id) FROM Post pst WHERE pst.user.id = u.id AND pst.deletedAt is NULL) as storyCount) " +
                                "FROM User u " +
                                "LEFT JOIN u.userImages ui " +
                                "LEFT JOIN u.points p " +
                                "WHERE u.id = :user_id AND u.deletedAt is NULL", UserInfo.class)
                .setParameter("user_id", targetId)
                .getSingleResult();

        Long accuRanking = (Long) entityManager.createQuery(
                        "SELECT rankings.AccuRanking as AccuRanking " +
                                "FROM ( " +
                                "   SELECT ROW_NUMBER() OVER (ORDER BY p.accumulate_point DESC) as AccuRanking, u.id as userId " +
                                "   FROM Point p " +
                                "   INNER JOIN p.user u " +
                                "   WHERE u.deletedAt IS NULL " +
                                ") rankings " +
                                "WHERE rankings.userId = :user_id")
                .setParameter("user_id", targetId)
                .getSingleResult();

        Long todayRanking = (Long) entityManager.createQuery(
                        "SELECT rankings.TodayRanking as TodayRanking " +
                                "FROM ( " +
                                "   SELECT ROW_NUMBER() OVER (ORDER BY subquery.storyCount DESC, subquery.accuPoint DESC) as TodayRanking, subquery.userId as userId " +
                                "   FROM ( " +
                                "       SELECT u.id as userId, p.accumulate_point as accuPoint, COUNT(pst.id) as storyCount " +
                                "       FROM User u " +
                                "       LEFT JOIN u.userImages ui " +
                                "       LEFT JOIN u.points p " +
                                "       LEFT JOIN Post pst ON pst.user.id = u.id AND FUNCTION('DATE_FORMAT', pst.createdAt, '%Y-%m-%d') = CURRENT_DATE " +
                                "       WHERE u.deletedAt IS NULL " +
                                "       GROUP BY u.id, p.accumulate_point " +
                                "   ) subquery " +
                                ") rankings " +
                                "WHERE rankings.userId = :user_id")
                .setParameter("user_id", targetId)
                .getSingleResult();

        if (todayRanking == null) {
            throw new NotFoundException("유저의 오늘 랭킹순위를 찾을 수 없습니다.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userInfo.getUserId());
        map.put("email", userInfo.getEmail());
        map.put("nickname", userInfo.getNickname());
        map.put("description", userInfo.getDescription());
        map.put("createdAt", userInfo.getCreatedAt());
        map.put("imageUrl", userInfo.getUserImage());
        map.put("storyCount", userInfo.getStoryCount());
        map.put("accuRanking", accuRanking);
        map.put("todayRanking", todayRanking);
        return map;
    }

    // 유저 정보 수정
    @Transactional
    public void setUser(Integer userId, Integer targetId, String description, MultipartFile userImage) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);
        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        User targetUser = userRepository.findByIdAndDeletedAtIsNull(targetId);
        UserImage targetUserImage = userImageRepository.findOneByUser_id(targetId);

        if (targetUser == null) {
            throw new NotFoundException("요청한 사용자를 찾을 수 없습니다.");
        }

        if (targetUserImage == null) {
            throw new NotFoundException("요청한 사용자의 이미지를 찾을 수 없습니다.");
        }

        // 파일을 저장하고 경로를 user 엔티티에 설정합니다.

        String imagePath = UserImage.saveUserImage(userImage);  // saveUserImage 메서드는 파일을 저장하고 경로를 반환하는 메서드입니다.

        targetUser.setDescription(description);
        userRepository.save(targetUser); // 변경된 내용 저장

        targetUserImage.setImage_url(imagePath);
        userImageRepository.save(targetUserImage);
    }

    // 유저 정보 삭제
    @Transactional
    public void delUser(Integer userId, Integer targetId) {
        boolean existUser = userRepository.existsByIdAndDeletedAtIsNull(userId);

        if (!existUser) {
            throw new UnauthorizedException("잘못된 또는 만료된 토큰입니다.");
        }

        User targetUser = userRepository.findByIdAndDeletedAtIsNull(targetId);

        if (targetUser == null) {
            throw new NotFoundException("요청한 사용자를 찾을 수 없습니다.");
        }

        targetUser.deleteSoftly(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 1000)
    public void fetchData(){
        List<User> newUsers = userRepository.findByIdGreaterThan(lastProcessedId);

        if (!newUsers.isEmpty()) {
            // 새로운 데이터가 있을 경우 처리
            for (User user : newUsers) {
                System.out.println("New user fetched: " + user.getNickname());
            }

            // 마지막으로 처리한 데이터의 ID 업데이트
            lastProcessedId = newUsers.get(newUsers.size() - 1).getId();
        }

    }
}
