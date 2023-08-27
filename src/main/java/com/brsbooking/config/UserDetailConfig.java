package com.brsbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class UserDetailConfig {

    private final JwtTokenHelper jwtTokenHelper;

    public UserDetailConfig(JwtTokenHelper jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    public UserDetails getUserInfo(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenHelper.extractUserDetailsFromToken(token);
    }
}
