package com.kubikdata.user.controller;

import com.kubikdata.model.User;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

@Validated
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class SignUpDTO extends User {

    public User toUser() {
        return User.builder().userName(getUserName()).name(getName()).pass(getPass()).email(getEmail()).surname(getSurname()).verified(false).build();
    }

}
