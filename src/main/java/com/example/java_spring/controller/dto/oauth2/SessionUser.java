package com.example.java_spring.controller.dto.oauth2;

import com.example.java_spring.jpa.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;
    // private String email;

    public SessionUser(User user){
        this.name = user.getNickname();
        // this.email = user.getEmail();
    }
}
