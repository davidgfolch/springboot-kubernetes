package com.dgf.controller;

import com.dgf.controller.model.ErrorResult;
import com.dgf.controller.model.FieldValidationError;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.IBaseEntity;
import com.dgf.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

import static com.dgf.controller.model.ErrorResult.VALIDATION_FIELD_ERRORS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final TranslationService translationSvc;

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @SuppressWarnings("unused")
    <T extends IBaseEntity> ResponseDTO<T> handleValidationErrors(WebExchangeBindException e) {
        List<FieldValidationError> errors = e.getBindingResult().getFieldErrors().stream().map(fieldError ->
                new FieldValidationError(fieldError.getField(),
                        translationSvc.removeParameters(fieldError.getDefaultMessage()),
                        translationSvc.translate(fieldError.getDefaultMessage()))
        ).collect(Collectors.toList());
        return new ResponseDTO<>(
                new ErrorResult(errors, e, VALIDATION_FIELD_ERRORS, translationSvc.translate(VALIDATION_FIELD_ERRORS)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @SuppressWarnings("unused")
    <T extends IBaseEntity> ResponseDTO<T> handleGeneralException(Exception e) {
        log.error("Unhandled exception", e);
        return new ResponseDTO<>(new ErrorResult(e));
    }

}