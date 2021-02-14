package com.kubikdata.controller;

import com.kubikdata.model.IBaseEntity;
import lombok.Getter;

@Getter
public class ResponseDTO {

    public static final String DEFAULT_OK = "ok";
    public static final String DEFAULT_ERROR = "error";

    private final String response;
    private final ErrorResult error;
    private final IBaseEntity entity;

    public ResponseDTO() {
        this.error=new ErrorResult();
        this.response= DEFAULT_OK;
        this.entity=null;
    }
    public ResponseDTO(ErrorResult error) {
        this.error=error;
        this.response= DEFAULT_ERROR;
        this.entity=null;
    }

    public ResponseDTO(IBaseEntity entity) {
        this.error=null;
        this.response = DEFAULT_OK;
        this.entity = entity;
    }
}
