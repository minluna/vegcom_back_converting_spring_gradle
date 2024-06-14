package com.example.springboot_gradle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing  // 데이터 생성, 수정값 자동입력 하는 기능
@EnableScheduling
public class SpringBootGradleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGradleApplication.class, args);
    }

}
