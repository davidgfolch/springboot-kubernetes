package com.kubikdata.controller;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class ErrorResult {

    @Getter(AccessLevel.PUBLIC)
    private final List<FieldValidationError> fieldValidation;
    private final Exception exception;

    @SuppressWarnings("unused")
    public ErrorResult() {
        this.fieldValidation=new ArrayList<>();
        this.exception=null;
    }

    public ErrorResult(Exception e) {
        this.fieldValidation=null;
        this.exception=e;
    }

    public ErrorResult(List<FieldValidationError> fieldValidation, Exception e) {
        this.fieldValidation=fieldValidation;
        this.exception=e;
    }
}
