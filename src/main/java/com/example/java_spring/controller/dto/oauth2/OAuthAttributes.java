package com.example.java_spring.controller.dto.oauth2;

import com.example.java_spring.jpa.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String socialName, String userNameAttributeName, Map<String, Object> attributes){
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        }
        // else if ("google".equals(socialName)) {
        //     return ofGoogle("sub", attributes);
        // } else if ("naver".equals(socialName)) {
        //     return ofNaver("id", attributes);
        // }

        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        // 추후 이메일로 생성 예정
        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                // .email((String) kakaoAccount.get("email"))
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    // private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
    //     return OAuthAttributes.builder()
    //             .name(String.valueOf(attributes.get("name")))
    //             .email(String.valueOf(attributes.get("email")))
    //             .profileImageUrl(String.valueOf(attributes.get("picture")))
    //             .attributes(attributes)
    //             .nameAttributesKey(userNameAttributeName)
    //             .build();
    // }


    // public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
    //     Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    //
    //     return OAuthAttributes.builder()
    //             .name(String.valueOf(response.get("nickname")))
    //             .email(String.valueOf(response.get("email")))
    //             .profileImageUrl(String.valueOf(response.get("profile_image")))
    //             .ageRange((String) response.get("age"))
    //             .gender((String) response.get("gender"))
    //             .attributes(response)
    //             .nameAttributesKey(userNameAttributeName)
    //             .build();
    // }

    public User toEntity() {
        return User.builder()
                .nickname(name)
                .email(email)
                .roles(List.of("USER"))
                .build();
    }
}
