package com.example.java_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // 데이터 생성, 수정값 자동입력 하는 기능
public class JavaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaSpringApplication.class, args);
    }

}
