package com.kubikdata.user.controller;

import com.kubikdata.controller.BaseController;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.model.User;
import com.kubikdata.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final BaseController<User> baseController;
    private final UserService srv;

    @PutMapping("/signup")
    public ResponseDTO<User> signup(@Valid @RequestBody SignUpDTO dto) {
        return baseController.exceptionHandling(() -> new ResponseDTO<>(
                srv.signup(dto.toUser())
        ));
    }

    @PostMapping("/login")
    public ResponseDTO<User> login(SignUpDTO dto) {
        return baseController.exceptionHandling(() -> new ResponseDTO<>(
                srv.login(dto.getUserName(), dto.getPass())
        ));
    }

    @GetMapping("/recover")
    public ResponseDTO<User> recover() {
        log.info("UserController recover");
        return baseController.exceptionHandling(ResponseDTO::new);
    }

}
