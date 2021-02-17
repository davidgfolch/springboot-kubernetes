package com.kubikdata.controller.model;

import com.kubikdata.model.IBaseEntity;
import com.kubikdata.util.JsonStringifier;
import lombok.Getter;

@Getter
public class ResponseDTO<T extends IBaseEntity> {

    public static final String DEFAULT_OK = "ok";
    public static final String DEFAULT_ERROR = "error";

    private final String response;
    private final ErrorResult error;
    private final T entity;

    public ResponseDTO() {
        this.error = null;
        this.response = DEFAULT_OK;
        this.entity = null;
    }

    public ResponseDTO(ErrorResult error) {
        this.error = error;
        this.response = DEFAULT_ERROR;
        this.entity = null;
    }

    public ResponseDTO(T entity) {
        this.error = null;
        this.response = DEFAULT_OK;
        this.entity = entity;
    }

    @Override
    public String toString() {
        return JsonStringifier.serialize(this);
    }
}
