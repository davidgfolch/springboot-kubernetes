package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.FieldValidationError;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.model.IBaseEntity;
import com.kubikdata.service.TranslationService;
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

    private final TranslationService translationService;

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    <T extends IBaseEntity> ResponseDTO<T> handleValidationErrors(WebExchangeBindException e) {
        List<FieldValidationError> errors = e.getBindingResult().getFieldErrors().stream().map(fieldError ->
                new FieldValidationError(fieldError.getField(),
                        translationService.removeParameters(fieldError.getDefaultMessage()),
                        translationService.translate(fieldError.getDefaultMessage()))
        ).collect(Collectors.toList());
        return new ResponseDTO<>(new ErrorResult(errors, e, VALIDATION_FIELD_ERRORS, translationService.translate(VALIDATION_FIELD_ERRORS)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    <T extends IBaseEntity> ResponseDTO<T> handleGeneralException(Exception e) {
        log.error("Unhandled exception", e);
        return new ResponseDTO<>(new ErrorResult(e));
    }

}