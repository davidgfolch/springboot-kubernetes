package com.kubikdata.controller;

import com.kubikdata.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({BaseController.class, TranslateService.class, TranslatorAOP.class})
public class BaseControllerTest {

    @Autowired
    protected WebTestClient webClient;

}
