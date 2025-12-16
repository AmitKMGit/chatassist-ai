package com.chatassist.backend.security.oauth2;

import com.chatassist.backend.model.User;
import com.chatassist.backend.model.User.AuthProvider;
import com.chatassist.backend.repo.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        CustomOAuth2User customUser = new CustomOAuth2User(oAuth2User);

        String email = customUser.getEmail();
        String providerId = customUser.getName(); // google sub

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, providerId, customUser));

        // if already exists but local provider, you can decide to restrict
        if (user.getProvider() != AuthProvider.GOOGLE) {
            // optional: throw custom exception if mixing providers not allowed
        }

        return customUser;
    }

    private User registerNewUser(String email, String providerId, CustomOAuth2User customUser) {
        User user = new User();
        user.setEmail(email);
        user.setName(customUser.getFullName());
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        // password remains null for Google users

        // set default ROLE_USER etc.
        return userRepository.save(user);
    }
}
