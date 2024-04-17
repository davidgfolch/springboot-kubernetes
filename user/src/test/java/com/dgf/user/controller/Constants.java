package com.dgf.user.controller;

import java.util.List;

public class Constants {

    public static final List<List<String>> DATA_FEEDER_USERS = List.of(
            List.of("davidgfolch", "davidgfolch@gmail.com", "David", "Garcia", "JJkskdghdkls1!"),
            List.of("John Mic", "johnmic@gmail.com", "John", "Mic", "AnotherPass1!"));
    public static final SignUpDTO SIGNUP_DTO = SignUpDTO.builder()
            .userName("davidgfolch")
            .email("davidgfolch@gmail.com")
            .name("David")
            .surname("Garcia")
            .pass("Invalid1Pass").build();

}
