package com.kubikdata.user.service;

import com.kubikdata.model.User;
import com.kubikdata.service.BaseService;
import com.kubikdata.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final String HIDDEN_PASS = "***************";
    public static final String VALIDATION_USER_PASS_DONT_MATCH = "validation.user.pass.dont.match";
    public static final String VALIDATION_USER_NOT_VERIFIED = "validation.user.notVerified";

    private final UserRepository repo;
    private final BaseService baseService;
    private final PasswordEncoder encoder;

    public User login(String userName, String pass) {
        User user = repo.findByUserName(userName);
        if (Objects.isNull(user) || !Objects.equals(pass, encoder.encode(user.getPass()))) {
            throw new UserBusinessException(VALIDATION_USER_PASS_DONT_MATCH);
        }
        if (!user.isVerified()) {
            throw new UserBusinessException(VALIDATION_USER_NOT_VERIFIED);
        }
        return user;
    }

    public User signup(User user) {
        //todo kafka client   messageSrv.send("email", user.getEmail());
        user.setPass(encoder.encode(user.getPass()));
        User result = baseService.checkConstraintException(() -> repo.save(user), User.CONSTRAINTS);
        result.setPass(HIDDEN_PASS);
        return result;
    }

}
