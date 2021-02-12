package com.kubikdata.user.service;

import com.kubikdata.model.User;
import com.kubikdata.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public User login(String userName, String pass) {
        User user = repo.findByUserName(userName);
        if (Objects.isNull(user) || !Objects.equals(pass,user.getPass())) {
            throw new UserServiceException("Invalid user or password");
        }
        if (!user.isVerified()) {
            throw new UserServiceException("User email not yet verified");
        }
        return user;
    }
}
