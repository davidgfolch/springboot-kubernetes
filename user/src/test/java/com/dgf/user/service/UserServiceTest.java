package com.dgf.user.service;

import com.dgf.model.User;
import com.dgf.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.Supplier;

import static com.dgf.user.service.UserService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final Supplier<User> USER_NOT_VERIFIED = () -> User.builder().userName("username").pass("password").verifyToken("token").build();
    public static final Supplier<User> USER_VERIFIED = () -> USER_NOT_VERIFIED.get().toBuilder().verifyToken(null).build();

    @Mock
    UserRepository repo;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks
    UserService svc;

    private void assertBusinessException(String expectedErrorCode, Supplier<?> s) {
        try {
            s.get();
            fail();
        } catch (UserBusinessException e) {
            assertEquals(expectedErrorCode, e.getMessage());
        }
    }

    @Test
    void login() {
        Supplier<User> loginFnc = () -> svc.login("username", "password");
        assertBusinessException(VALIDATION_USER_PASS_DONT_MATCH, loginFnc);
        mockFindUserByName(USER_NOT_VERIFIED);
        mockEncoderMatches(false);
        assertBusinessException(VALIDATION_USER_PASS_DONT_MATCH, loginFnc);
        mockEncoderMatches(true);
        assertBusinessException(VALIDATION_USER_NOT_VERIFIED, loginFnc);
        mockFindUserByName(USER_VERIFIED);
        var user = loginFnc.get();
        assertEquals("username", user.getUserName());
    }

    private void mockEncoderMatches(boolean value) {
        when(encoder.matches(anyString(), anyString())).thenReturn(value);
    }

    private void mockFindUserByName(Supplier<User> user) {
        User instance = user.get();
        when(repo.findByUserName(anyString())).thenReturn(Optional.ofNullable(instance));
    }

    @Test
    void signup() {
        User user = USER_VERIFIED.get();
        when(encoder.encode(anyString())).thenReturn("encodedPwd");
        User userEncPwd = user.toBuilder().pass("encodedPwd").build();
        when(repo.save(user)).thenReturn(userEncPwd);
        spy(repo).save(userEncPwd);
        User userHiddenPwd = userEncPwd.toBuilder().pass(HIDDEN_PASS).build();
        assertEquals(userHiddenPwd, svc.signup(user));
    }

    @Test
    void verify() {
        User verified = USER_VERIFIED.get();
        mockFindUserByName(USER_VERIFIED);
        assertBusinessException(VALIDATION_USER_ALREADY_VERIFIED, () -> verifyUser(verified));
        mockFindUserByName(() -> USER_NOT_VERIFIED.get().toBuilder().verifyToken("invalidToken").build());
        assertBusinessException(VALIDATION_USER_INVALID_VERIFY_TOKEN, () -> verifyUser(USER_NOT_VERIFIED.get()));
        mockFindUserByName(USER_NOT_VERIFIED);
        when(repo.save(verified)).thenReturn(verified);
        var user = verifyUser(USER_NOT_VERIFIED.get());
        assertEquals(verified, user);
    }

    private User verifyUser(User user) {
        return svc.verify(user.getUserName(), user.getVerifyToken());
    }

    @Test
    void deleteIfExists() {
        mockFindUserByName(() -> null);
        assertFalse(svc.deleteIfExists("userName"));
        mockFindUserByName(USER_VERIFIED);
        assertTrue(svc.deleteIfExists("userName"));
    }
}