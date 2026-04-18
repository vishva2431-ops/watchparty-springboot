package com.vish.watchparty.config;

import com.vish.watchparty.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/movies/**", "/rooms/**", "/uploads/**", "/admin/**", "/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                        .defaultSuccessUrl("http://localhost:5180/home", true)
//                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}