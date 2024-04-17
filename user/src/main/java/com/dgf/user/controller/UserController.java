package com.dgf.user.controller;

import com.dgf.controller.BaseController;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.User;
import com.dgf.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final BaseController<User> baseCtrl;
    private final UserService srv;

    @PutMapping("/signup")
    public ResponseDTO<User> signup(@Valid @RequestBody SignUpDTO dto) {
        log.info("signup from -> {}", getPodName());
        return baseCtrl.exceptionHandling(() -> new ResponseDTO<>(
                srv.signup(dto.toUser())
        ));
    }

    @PostMapping("/login")
    public ResponseDTO<User> login(@Valid @RequestBody LoginDTO dto) {
        log.info("login {} (passssssh!: {}) from -> {}", dto.getUserName(), dto.getPass(), getPodName());
        return baseCtrl.exceptionHandling(() -> new ResponseDTO<>(
                srv.login(dto.getUserName(), dto.getPass())
        ));
    }

    @PostMapping("/verify")
    public ResponseDTO<User> verify(@Valid @RequestBody VerifyDTO dto) {
        log.info("verify user {} with token {} from -> {}", dto.getUserName(), dto.getToken(), getPodName());
        return new ResponseDTO<>(srv.verify(dto.getUserName(), dto.getToken()));
    }

    @GetMapping("/recover")
    public ResponseDTO<User> recover() {
        log.info("recover from -> {}", getPodName());
        return baseCtrl.exceptionHandling(ResponseDTO::new);
    }

    @GetMapping("/hello")
    @SuppressWarnings("unused")
    public String hello() {
        log.info("hello from -> {}", getPodName());
        return "hello from -> " + getPodName();
    }

    private String getPodName() {
        return ofNullable(System.getenv("HOSTNAME")).orElse("TEST");
    }

    //todo move out of production scope?
    public void deleteIfExists(SignUpDTO dto) {
        srv.deleteIfExists(dto.getUserName());
    }
}
