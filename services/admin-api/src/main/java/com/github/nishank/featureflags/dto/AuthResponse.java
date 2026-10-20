package com.github.nishank.featureflags.dto;

import com.github.nishank.featureflags.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private UserDto user;

    public static AuthResponse of(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(UserDto.from(user))
                .build();
    }
}
