package com.kubikdata.user.resource;

import com.kubikdata.model.User;
import com.kubikdata.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserResource {

	private final UserService srv;

	@PutMapping("/singup")
	public String singup(@Valid UserDTO dto) {
		return "singup";
	}

	@PostMapping("/login")
	public UserDTO login(UserDTO dto) {
		User entity = srv.login(dto.getUserName(), dto.getPass());
		return (UserDTO) entity;
	}

	@GetMapping("/recover")
	public String recover() {
		log.info("UserResource recover");
		return "recover";
	}

}
