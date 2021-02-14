package com.kubikdata.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO handleMethodArgumentNotValidException(WebExchangeBindException e) {
        return new ResponseDTO(new ErrorResult(
                e.getBindingResult().getFieldErrors().stream().map(fieldError ->
                        new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage())
                ).collect(Collectors.toList()), e));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO handleMethodArgumentNotValidException(Exception e) {
        log.error("Unhandled exception", e);
        return new ResponseDTO(new ErrorResult(e));
    }

}