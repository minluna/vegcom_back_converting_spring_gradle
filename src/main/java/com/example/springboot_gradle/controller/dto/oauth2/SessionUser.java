package com.example.springboot_gradle.controller.dto.oauth2;

import com.example.springboot_gradle.jpa.entity.User;
import lombok.Getter;

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
