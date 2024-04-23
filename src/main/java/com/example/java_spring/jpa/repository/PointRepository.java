package com.example.java_spring.jpa.repository;

import com.example.java_spring.jpa.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Point findOneById(Integer userId);
}
