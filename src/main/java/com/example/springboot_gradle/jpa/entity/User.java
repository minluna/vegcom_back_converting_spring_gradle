package com.example.springboot_gradle.jpa.entity;

import com.example.springboot_gradle.controller.dto.user.CreateUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)  // Entity를 DB에 적용하기 이전, 이후에 커스텀 콜백을 적용하기 위한 클래스
@DynamicInsert  // insert 시 null값인 컬럼은 insert문에서 제외
@DynamicUpdate  // update 시 전체 update가 아닌 변경된 데이터만 update
@Builder
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "user_id")
    @Comment("유저의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("유저의 이메일")
    private String email;

    @Column(nullable = false)
    @Comment("유저의 비밀번호")
    private String password;

    @Column(nullable = false)
    @Comment("유저의 별명")
    private String nickname;

    @Column
    @ColumnDefault("''")  // 컬럼 기본값 지정
    @Comment("유저의 설명")
    private String description;

    @CreatedDate    // 생성일자 자동 입력
    @Column(nullable = false, updatable = false)    // 생성일자는 수정 불가능하게 설정
    @Comment("회원가입 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate   // 수정일자 자동 입력
    @Column(nullable = false)
    @Comment("유저 정보 업데이트 시간")
    private LocalDateTime updatedAt;

    @Column
    @Comment("회원탈퇴 시간")
    private LocalDateTime deletedAt;

    // 삭제
    public void deleteSoftly(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // 확인
    public boolean isSoftDeleted() {
        return null != deletedAt;
    }

    @OneToOne(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private Point points;

    @OneToOne(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage userImages;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostComment> postComments;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> postLikes;


    // 멤버가 가지고 있는 권한(authority) 목록을 SimpleGrantedAuthority로 변환하여 반환하기 위한 메서드
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    // 회원가입을 위해 만든 메서드
    public static User registerUser(CreateUser createUser) {
        User user = new User();
        user.setEmail(createUser.getEmail());
        user.setPassword(createUser.getPassword());
        user.setNickname(createUser.getNickname());

        // 기본 권한 설정
        user.setRoles(new ArrayList<>());
        user.getRoles().add("ROLE_USER");

        return user;
    }

    // username을 안쓰기 위해 만든 메서드
    @Override
    public String getUsername() {
        return email;
    }
}
