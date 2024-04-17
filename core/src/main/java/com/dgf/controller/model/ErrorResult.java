package com.dgf.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@JsonIgnoreProperties("exception")
public class ErrorResult {

    public static final String VALIDATION_FIELD_ERRORS = "validation.field.errors";

    private final List<FieldValidationError> fieldValidationErrors;
    private final Exception exception;
    private final String code;
    @Setter
    private String message;

    @SuppressWarnings("unused") //used in serialization
    public ErrorResult() {
        this(new ArrayList<>(), null, null);
    }

    public ErrorResult(Exception e, String code, String message) {
        this(new ArrayList<>(), e, code, message);
    }

    public ErrorResult(Exception e) {
        this(new ArrayList<>(), e, e.getMessage(), null);
    }

    public ErrorResult(List<FieldValidationError> fieldValidationErrors, Exception e, String code) {
        this(fieldValidationErrors,e, code,"");
    }

    public ErrorResult(List<FieldValidationError> fieldValidationErrors, Exception e, String code, String message) {
        this.fieldValidationErrors = fieldValidationErrors;
        this.exception=e;
        this.code = code;
        this.message=message;
    }
}
