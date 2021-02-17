package com.kubikdata.controller;

import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.model.IBaseEntity;
import com.kubikdata.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Import({BaseController.class, TranslationService.class, TranslationAOP.class})
public abstract class BaseControllerTest<T extends IBaseEntity> {

    @Autowired
    protected WebTestClient webClient;

    public ResponseDTO<T> put(String uri, IBaseEntity body, HttpStatus respStatus, ParameterizedTypeReference<ResponseDTO<T>> responseType) {
        return exchange(PUT, uri, body, respStatus, responseType);
    }

    public ResponseDTO<T> exchange(HttpMethod method, String uri, IBaseEntity body, HttpStatus respStatus, ParameterizedTypeReference<ResponseDTO<T>> responseType) {
        return webClient.method(method).uri(uri).accept(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(respStatus)
                .expectBody(responseType)
                .returnResult().getResponseBody();
    }

}
