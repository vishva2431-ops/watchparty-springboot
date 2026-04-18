package com.vish.watchparty.service;

import com.vish.watchparty.model.User;
import com.vish.watchparty.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email != null && !email.isBlank()) {
            userRepository.findByEmail(email)
                    .map(existing -> {
                        existing.setName(name);
                        existing.setEmail(email);
                        existing.setProvider("GOOGLE");
                        existing.setLoginMethod("GOOGLE");
                        existing.setRole("USER");
                        return userRepository.save(existing);
                    })
                    .orElseGet(() -> userRepository.save(
                            User.builder()
                                    .name(name)
                                    .email(email)
                                    .provider("GOOGLE")
                                    .loginMethod("GOOGLE")
                                    .role("USER")
                                    .build()
                    ));
        }

        return oauth2User;
    }
}