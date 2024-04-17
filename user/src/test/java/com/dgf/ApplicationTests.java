package com.dgf;

import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.User;
import com.dgf.user.controller.LoginDTO;
import com.dgf.user.controller.SignUpDTO;
import com.dgf.user.controller.UserController;
import com.dgf.user.controller.VerifyDTO;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.dgf.controller.model.ResponseDTO.DEFAULT_ERROR;
import static com.dgf.controller.model.ResponseDTO.DEFAULT_OK;
import static com.dgf.model.User.VALIDATION_USER_USER_NAME;
import static com.dgf.user.controller.Constants.DATA_FEEDER_USERS;
import static com.dgf.user.controller.Constants.SIGNUP_DTO;
import static com.dgf.user.service.UserService.VALIDATION_USER_NOT_VERIFIED;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ApplicationTests {

    public AtomicReference<User> USER_VALID = new AtomicReference<>(null);

    @Autowired
    @SuppressWarnings("unused")
    private UserController ctrl;

    private void assertResponseOK(ResponseDTO<User> res) {
        assertEquals(DEFAULT_OK, res.getResponse());
    }

    private void assertResponseError(ResponseDTO<User> res, String code, String trans) {
        assertEquals(DEFAULT_ERROR, res.getResponse());
        var error = res.getError();
        assertNotNull(res.getError());
        assertNotNull(error.getException());
        assertEquals(code, error.getCode());
        assertEquals(trans, error.getMessage());
    }

    @Order(0)
    @ParameterizedTest
    @MethodSource
    void dataFeeder(SignUpDTO dto) {
        ctrl.deleteIfExists(dto);
        var res = ctrl.signup(dto);
        assertResponseOK(res);
        var user = res.getEntity();
        assertNotNull(user);
        if (isNull(USER_VALID.get()))
            USER_VALID.getAndSet(user);
    }

    static Stream<SignUpDTO> dataFeeder() {
        return DATA_FEEDER_USERS.stream().map(ApplicationTests::dataFeederToUser);
    }

    private static SignUpDTO dataFeederToUser(List<String> userFields) {
        return SignUpDTO.builder()
                .userName(userFields.get(0))
                .email(userFields.get(1))
                .name(userFields.get(2))
                .surname(userFields.get(3))
                .pass(userFields.get(4)).build();
    }

    @Order(0)
    @Test
    void baseService_checkConstraintException() {
        var signupDto = SIGNUP_DTO.toBuilder().pass("12345aA.").build();
        var signupRes = ctrl.signup(signupDto);
        assertResponseOK(signupRes);
        var res = ctrl.signup(signupDto);
        assertResponseError(res, VALIDATION_USER_USER_NAME, "Invalid username (already exists)");
    }

    @Order(1)
    @Test
    void loginUnverifiedUser() {
        var signUpDTO = createUserIfNotExists();
        var user = USER_VALID.get();
        var res = ctrl.login(new LoginDTO(user.getUserName(), signUpDTO.getPass()));
        assertResponseError(res,VALIDATION_USER_NOT_VERIFIED,"User email not yet verified");
    }

    @Order(2)
    @Test
    void userVerifyAndLogin() {
        var signUpDTO = createUserIfNotExists();
        var user = USER_VALID.get();
        var resVerify = ctrl.verify(new VerifyDTO(user.getUserName(), user.getVerifyToken()));
        assertResponseOK(resVerify);
        var res = ctrl.login(new LoginDTO(user.getUserName(), signUpDTO.getPass()));
        assertResponseOK(res);
    }

    private SignUpDTO createUserIfNotExists() {
        var signUpDTO = dataFeederToUser(DATA_FEEDER_USERS.get(0));
        if (isNull(USER_VALID.get()))
            dataFeeder(signUpDTO);
        return signUpDTO;
    }

}
