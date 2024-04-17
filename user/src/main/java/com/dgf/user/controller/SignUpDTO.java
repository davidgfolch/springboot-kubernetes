package com.dgf.user.controller;

import com.dgf.model.User;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class SignUpDTO extends User {

    public User toUser() {
        return User.builder()
                .userName(getUserName())
                .name(getName())
                .pass(getPass())
                .email(getEmail())
                .surname(getSurname())
                .verifyToken(UUID.randomUUID().toString()).build();
    }

}
