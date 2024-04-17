package com.dgf.controller;

import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.IBaseEntity;
import com.dgf.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Import({BaseController.class, TranslationService.class, TranslationAOP.class})
public abstract class BaseControllerTest {

    @Autowired
    protected WebTestClient webClient;

    public <T extends IBaseEntity> ResponseDTO<T> put(String uri, Object body, HttpStatus respStatus, ParameterizedTypeReference<ResponseDTO<T>> responseType) {
        return exchange(PUT, uri, body, respStatus, responseType);
    }

    public <T extends IBaseEntity> ResponseDTO<T> post(String uri, Object body, HttpStatus respStatus, ParameterizedTypeReference<ResponseDTO<T>> responseType) {
        return exchange(POST, uri, body, respStatus, responseType);
    }

    public <T extends IBaseEntity> ResponseDTO<T> exchange(HttpMethod method, String uri, Object body, HttpStatus respStatus, ParameterizedTypeReference<ResponseDTO<T>> responseType) {
        return webClient.method(method).uri(uri).accept(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(respStatus)
                .expectBody(responseType)
                .returnResult().getResponseBody();
    }

}
