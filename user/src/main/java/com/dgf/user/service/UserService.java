package com.dgf.user.service;

import com.dgf.model.User;
import com.dgf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.dgf.model.User.CONSTRAINTS;
import static com.dgf.service.BaseService.checkDataIntegrityViolationException;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String HIDDEN_PASS = "***************";
    public static final String VALIDATION_USER_PASS_DONT_MATCH = "validation.user.pass.dont.match";
    public static final String VALIDATION_USER_NOT_VERIFIED = "validation.user.notVerified";
    public static final String VALIDATION_USER_NOT_FOUND = "validation.user.notFound";
    public static final String VALIDATION_USER_ALREADY_VERIFIED = "validation.user.already.verified";
    public static final String VALIDATION_USER_INVALID_VERIFY_TOKEN = "validation.user.invalid.verify.token";

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public User login(String userName, String pass) {
        return repo.findByUserName(userName).map(user -> {
            if (!encoder.matches(pass, user.getPass())) {
                log.info("UserName {}, INVALID PASS {} ", userName, pass);
                throw new UserBusinessException(VALIDATION_USER_PASS_DONT_MATCH);
            }
            if (Objects.nonNull(user.getVerifyToken())) {
                throw new UserBusinessException(VALIDATION_USER_NOT_VERIFIED);
            }
            return user;
        }).orElseThrow(() -> {
            log.info("UserName {} NOT FOUND", userName);
            return new UserBusinessException(VALIDATION_USER_PASS_DONT_MATCH);
        });
    }

    public User signup(User user) {
        //todo kafka client   messageSrv.send("email", user.getEmail());
        log.info(user.toString());
        user.setPass(encoder.encode(user.getPass()));
        User result = checkDataIntegrityViolationException(() -> repo.save(user), CONSTRAINTS);
        result.setPass(HIDDEN_PASS);
        return result;
    }

    public User verify(String userName, String token) {
        return repo.findByUserName(userName).map(user -> {
                    if (isNull(user.getVerifyToken()))
                        throw new UserBusinessException(VALIDATION_USER_ALREADY_VERIFIED);
                    if (!user.getVerifyToken().equals(token))
                        throw new UserBusinessException(VALIDATION_USER_INVALID_VERIFY_TOKEN);
                    return repo.save(user.toBuilder().verifyToken(null).build());
                }).orElseThrow(() -> new UserBusinessException(VALIDATION_USER_NOT_FOUND));
    }

    //todo move out of production scope?
    public boolean deleteIfExists(String userName) {
        return repo.findByUserName(userName).map(user -> {
            repo.delete(user);
            return true;
        }).orElse(false);
    }
}
