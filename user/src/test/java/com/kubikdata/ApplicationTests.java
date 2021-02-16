package com.kubikdata;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.user.controller.SignUpDTO;
import com.kubikdata.user.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.kubikdata.controller.model.ResponseDTO.DEFAULT_ERROR;
import static com.kubikdata.controller.model.ResponseDTO.DEFAULT_OK;
import static com.kubikdata.model.User.VALIDATION_USER_USER_NAME;
import static com.kubikdata.user.controller.Constants.SIGNUP_DTO;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private UserController userController;

    /**
     * Signup twice to check exception handling
     */
    @Test
    void baseService_checkConstraintException() {
        SignUpDTO signupDto = SIGNUP_DTO.toBuilder().pass("12345aA.").build();
        ResponseDTO signup = userController.signup(signupDto);
        assertEquals(DEFAULT_OK, signup.getResponse());
        ResponseDTO response = userController.signup(signupDto);
        assertEquals(DEFAULT_ERROR, response.getResponse());
        ErrorResult error = response.getError();
        assertNotNull(response.getError());
        assertNotNull(error.getException());
        assertEquals(VALIDATION_USER_USER_NAME, error.getCode());
        assertEquals("Invalid username (already exists)", error.getMessage());
    }

}
