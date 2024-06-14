package com.example.springboot_gradle.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 사용
    @Column(name = "point_id")
    @Comment("포인트의 ID")
    private Integer id;

    @Column(nullable = false)   // not-null 설정
    @Comment("포인트를 소유한 유저의 ID")
    private Integer user_id;

    @Column(nullable = false)
    @Comment("유저의 현재 포인트")
    private Integer current_point;

    @Column(nullable = false)
    @Comment("유저의 누적 포인트")
    private Integer accumulate_point;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // 외래키 설정
    private User user;

    public static Point registerUser(Integer user_id) {
        Point userPoint = new Point();
        userPoint.setUser_id(user_id);
        userPoint.setCurrent_point(0);
        userPoint.setAccumulate_point(0);

        return userPoint;
    }
}
