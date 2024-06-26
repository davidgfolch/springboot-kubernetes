package com.dgf.controller.model;

import com.dgf.util.JsonStringifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class FieldValidationError {

    private final String field;
    private final String code;
    private final String message;

    @Override
    public String toString() {
        return JsonStringifier.serialize(this);
    }
}
