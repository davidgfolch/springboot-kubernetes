package com.kubikdata.user.controller;

import com.kubikdata.controller.BaseControllerTest;
import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.FieldValidationError;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kubikdata.controller.model.ErrorResult.VALIDATION_FIELD_ERRORS;
import static com.kubikdata.model.Constants.VALIDATION_MIN_SIZE;
import static com.kubikdata.model.User.*;
import static com.kubikdata.user.controller.Constants.SIGNUP_DTO;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
    private UserController res;
    @MockBean
    private UserService srv;

    @TestFactory
    Collection<DynamicTest> signupValidationOK() {
        return PASSWORD_SPECIAL_CHARS.chars().mapToObj(i -> (char) i).distinct()
                .map(specChar -> SIGNUP_DTO.toBuilder().pass("12345aA" + specChar).build())
                .map(dto ->
                        dynamicTest("validPass-specialChar " + dto.getPass(), () -> signupOk(dto))
                ).collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> signupValidationKO() {
        return INVALID_PASS.stream().map(fieldValidationError ->
                dynamicTest("invalidPass " + fieldValidationError.get(0).getField(), () -> signup_invalidPass(fieldValidationError))
        ).collect(Collectors.toList());
    }

    private void signupOk(SignUpDTO signUpDTO) {
        webClient.put().uri("/signup").accept(APPLICATION_JSON)
                .bodyValue(signUpDTO)
                .exchange()
                .expectStatus().isOk();
        ResponseDTO responseBody = res.signup(signUpDTO);
        assertEquals(ResponseDTO.DEFAULT_OK, responseBody.getResponse());
    }

    private void signup_invalidPass(List<FieldValidationError> expectedValidationList) {
        SignUpDTO dto = SIGNUP_DTO.toBuilder().pass(expectedValidationList.get(0).getField()).build();
        ResponseDTO responseBody = webClient.put().uri("/signup").accept(APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(responseBody);
        ErrorResult error = responseBody.getError();
        assertNotNull(error);
        assertEquals(VALIDATION_FIELD_ERRORS, error.getCode());
        assertEquals("Field's validation errors", error.getMessage());
        List<FieldValidationError> fieldValidation = error.getFieldValidation();
        assertEquals(expectedValidationList.size(), fieldValidation.size());
        fieldValidation.forEach(validation -> {
            log.debug("Asserting {} ", validation);
            Optional<FieldValidationError> first = expectedValidationList.stream().filter(expectedValidation -> expectedValidation.getErrorCode().equals(validation.getErrorCode())).findFirst();
            assertTrue(first.isPresent());
            checkFieldValidation(validation, first.get());
        });
        assertThrows(ValidationException.class, () -> res.signup(dto));
    }

    private void checkFieldValidation(FieldValidationError validation, FieldValidationError expected) {
        log.debug("Asserting {} vs expected {}", validation, expected);
        assertEquals("pass", validation.getField());
        assertThat(validation.getErrorCode(), matchesPattern(expected.getErrorCode()));
        assertThat(validation.getMessage(), matchesPattern(expected.getMessage()));
    }

    @Test
    void login() {
        when(srv.login(anyString(), anyString())).thenReturn(SIGNUP_DTO);
        ResponseDTO response = res.login(SIGNUP_DTO);
        assertEquals(ResponseDTO.DEFAULT_OK, response.getResponse());
        assertEquals(SIGNUP_DTO, response.getEntity());
    }

    @Test
    void recover() {
        assertEquals(ResponseDTO.DEFAULT_OK, res.recover().getResponse());
    }

}