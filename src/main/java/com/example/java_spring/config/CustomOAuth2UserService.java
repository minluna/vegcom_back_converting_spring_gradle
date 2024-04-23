package com.example.java_spring.config;

import com.example.java_spring.controller.dto.oauth2.OAuthAttributes;
import com.example.java_spring.controller.dto.oauth2.SessionUser;
import com.example.java_spring.jpa.entity.User;
import com.example.java_spring.jpa.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest); // Oath2 정보를 가져옴

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 소셜 정보 가져옴
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("ROLE_USER", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoles().toString())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * 이미 존재하는 회원이라면 이름과 프로필이미지를 업데이트해줍니다.
     * 처음 가입하는 회원이라면 User 테이블을 생성합니다.
     **/
    // 추후 email검증으로 변경 예정
    private User saveOrUpdate(OAuthAttributes attributes){
        User user =  userRepository.findByNickname(attributes.getName());
                // .map(entity -> entity.update(attributes.getName()))
                // .orElse(attributes.toEntity());
        if (user == null) {
            User createUser = new User();
            createUser.setEmail("oauth2@kakao.com");
            createUser.setPassword("oauth2loginpassword");
            createUser.setNickname(attributes.getName());

            // 기본 권한 설정
            createUser.setRoles(new ArrayList<>());
            createUser.getRoles().add("ROLE_USER");

            return userRepository.save(createUser);
        } else {
            return userRepository.save(user);

        }

    }
}
