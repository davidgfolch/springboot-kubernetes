package com.dgf.user.controller;

import com.dgf.controller.BaseControllerTest;
import com.dgf.controller.model.ErrorResult;
import com.dgf.controller.model.FieldValidationError;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.User;
import com.dgf.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.dgf.controller.model.ErrorResult.VALIDATION_FIELD_ERRORS;
import static com.dgf.controller.model.ResponseDTO.DEFAULT_OK;
import static com.dgf.model.Constants.VALIDATION_MIN_SIZE;
import static com.dgf.model.User.*;
import static com.dgf.user.controller.Constants.SIGNUP_DTO;
import static com.dgf.user.service.UserService.HIDDEN_PASS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@WebFluxTest(UserController.class)
@Slf4j
class UserControllerTest extends BaseControllerTest {

    private static final FieldValidationError fieldValidationError = new FieldValidationError(SIGNUP_DTO.getPass(),
            VALIDATION_USER_PASSWORD,
            "Invalid password, it should be at least (\\d+) characters length, contain upper/lower case letters, numbers & special character: .*");

    private static final List<List<FieldValidationError>> INVALID_PASS = asList(
            singletonList(fieldValidationError.toBuilder().field(SIGNUP_DTO.getPass()).build()),
            singletonList(fieldValidationError.toBuilder().field(PASSWORD_SPECIAL_CHARS).build()),
            singletonList(fieldValidationError.toBuilder().field("12345678aA").build()),
            singletonList(fieldValidationError.toBuilder().field("1 z Z 4 5 #").build()),
            asList(
                    new FieldValidationError("1aA.", VALIDATION_MIN_SIZE, "Must be at least " + PASSWORD_MIN_SIZE + " characters long"),
                    fieldValidationError.toBuilder().field("1aA.").build()
            )
    );

    @Autowired
    @SuppressWarnings("unused")
    private UserController ctrl;
    @MockBean
    @SuppressWarnings("unused")
    private UserService srv;

    @TestFactory
    Collection<DynamicTest> signupValidationOK() {
        return PASSWORD_SPECIAL_CHARS.chars().mapToObj(i -> (char) i).distinct()
                .map(specChar -> SIGNUP_DTO.toBuilder().pass("12345aA" + specChar).build())
                .map(dto ->
                        dynamicTest("validPass-specialChar " + dto.getPass(), () -> signupOk(dto))
                ).collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> signupValidationKO() {
        return INVALID_PASS.stream().map(fieldValidationError ->
                dynamicTest("invalidPass " + fieldValidationError.get(0).getField(), () -> signup_invalidPass(fieldValidationError))
        ).collect(toList());
    }

    private void signupOk(SignUpDTO signUpDTO) {
        SignUpDTO dto = signUpDTO.toBuilder().pass(HIDDEN_PASS).build();
        when(srv.signup(any(User.class))).thenReturn(dto.toUser());
        ResponseDTO<SignUpDTO> res = put("/signup", signUpDTO, OK, new ParameterizedTypeReference<>() {
        });
        assertEquals(DEFAULT_OK, res.getResponse());
        SignUpDTO entity = res.getEntity();
        assertNull(res.getError());
        assertNotNull(entity);
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getPass(), entity.getPass());
        assertEquals(dto.getUserName(), entity.getUserName());
        assertEquals(dto.getEmail(), entity.getEmail());
    }

    private void signup_invalidPass(List<FieldValidationError> expectedValidationList) {
        SignUpDTO dto = SIGNUP_DTO.toBuilder().pass(expectedValidationList.get(0).getField()).build();
        ResponseDTO<SignUpDTO> res = put("/signup", dto, BAD_REQUEST, new ParameterizedTypeReference<>() {
        });
        assertNotNull(res);
        ErrorResult error = res.getError();
        assertNotNull(error);
        assertEquals(VALIDATION_FIELD_ERRORS, error.getCode());
        assertEquals("Field's validation errors", error.getMessage());
        List<FieldValidationError> fieldErrors = error.getFieldValidationErrors();
        assertEquals(expectedValidationList.size(), fieldErrors.size());
        fieldErrors.forEach(validation -> {
            log.debug("Asserting {} ", validation);
            Optional<FieldValidationError> fieldError = expectedValidationList.stream()
                    .filter(expectedValidation -> expectedValidation.getCode().equals(validation.getCode())).findFirst();
            assertTrue(fieldError.isPresent());
            checkFieldValidation(validation, fieldError.get());
        });
        assertThrows(ValidationException.class, () -> ctrl.signup(dto));
    }

    private void checkFieldValidation(FieldValidationError validation, FieldValidationError expected) {
        log.debug("Asserting {} vs expected {}", validation, expected);
        assertEquals("pass", validation.getField());
        assertThat(validation.getCode(), matchesPattern(expected.getCode()));
        assertThat(validation.getMessage(), matchesPattern(expected.getMessage()));
    }

    @Test
    void login() {
        when(srv.login(anyString(), anyString())).thenReturn(SIGNUP_DTO.toUser());
        ResponseDTO<User> res = post("/login", new LoginDTO("userName", "Passwordd01!"), OK, new ParameterizedTypeReference<>() {
        });
        assertEquals(DEFAULT_OK, res.getResponse());
        var entity = res.getEntity();
        assertNotNull(entity.getVerifyToken());
        SignUpDTO expected = SIGNUP_DTO.toBuilder().verifyToken(entity.getVerifyToken()).build();
        assertEquals(expected, entity);
    }

    @Test
    void verify() {
        when(srv.verify(anyString(), anyString())).thenReturn(SIGNUP_DTO.toUser());
        ResponseDTO<User> res = post("/verify", new VerifyDTO("userName", "Passwordd01!"), OK, new ParameterizedTypeReference<>() {
        });
        assertEquals(DEFAULT_OK, res.getResponse());
    }

    @Test
    void recover() {
        assertEquals(DEFAULT_OK, ctrl.recover().getResponse());
    }

}