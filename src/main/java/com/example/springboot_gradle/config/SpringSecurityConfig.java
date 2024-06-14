package com.example.springboot_gradle.config;

import com.example.springboot_gradle.config.jwt.JwtAccessDeniedHandler;
import com.example.springboot_gradle.config.jwt.JwtAuthenticationEntryPoint;
import com.example.springboot_gradle.config.jwt.JwtAuthenticationFilter;
import com.example.springboot_gradle.config.jwt.JwtTokenProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    private ApplicationContext context;
    private JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public SpringSecurityConfig(ApplicationContext context, JwtTokenProvider jwtTokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.context = context;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    //SecurityFilterChain에 Bean으로 등록하는 과정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(AbstractHttpConfigurer::disable)

                // enable h2-console
                // .headers(headers->
                //         headers.contentTypeOptions(contentTypeOptionsConfig ->
                //                 headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))

                // disable session
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorizeRequests->
                        authorizeRequests
                                // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다.
                                .requestMatchers("/users/**", "/error/**", "/favicon.ico").permitAll()
                                // .requestMatchers(PathRequest.toH2Console()).permitAll()// h2-console, favicon.ico 요청 인증 무시
                                // OAuth2 로그인 허용
                                .requestMatchers("/oauth2/**").permitAll()
                                // 로그인, 회원가입 허용
                                .requestMatchers("/user/login", "/user/register").permitAll()
                                .anyRequest().authenticated() // 그 외 인증 없이 접근X
                )

                // OAuth2 설정
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .successHandler(customSuccessHandler(jwtTokenProvider,authenticationManagerBuilder))
                                .failureHandler(customFailureHandler())
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(context.getBean(CustomOAuth2UserService.class))))   // OAuth2UserService Bean 등록

                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptionHandling -> // 컨트롤러의 예외처리를 담당하는 exception handler와는 다름.
                        exceptionHandling
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                // .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );



        return httpSecurity.build();
    }

    @Bean
    public CustomFailureHandler customFailureHandler() {
        return new CustomFailureHandler();
    }

    @Bean
    public CustomSuccessHandler customSuccessHandler(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        return new CustomSuccessHandler(jwtTokenProvider, authenticationManagerBuilder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
