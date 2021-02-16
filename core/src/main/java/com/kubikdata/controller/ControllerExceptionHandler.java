package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.FieldValidationError;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

import static com.kubikdata.controller.model.ErrorResult.VALIDATION_FIELD_ERRORS;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final TranslateService translateService;

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO handleValidationErrors(WebExchangeBindException e) {
        List<FieldValidationError> errors = e.getBindingResult().getFieldErrors().stream().map(fieldError ->
                new FieldValidationError(fieldError.getField(),
                        translateService.removeParameters(fieldError.getDefaultMessage()),
                        translateService.translate(fieldError.getDefaultMessage()))
        ).collect(Collectors.toList());
        return new ResponseDTO(new ErrorResult(errors, e, VALIDATION_FIELD_ERRORS, translateService.translate(VALIDATION_FIELD_ERRORS)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO handleGeneralException(Exception e) {
        log.error("Unhandled exception", e);
        return new ResponseDTO(new ErrorResult(e));
    }

}