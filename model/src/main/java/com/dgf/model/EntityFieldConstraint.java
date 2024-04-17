package com.dgf.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EntityFieldConstraint {

    private final String constraintName;
    private final String code;

    public static EntityFieldConstraint create(String constraintName, String errorCode) {
        return new EntityFieldConstraint(constraintName,errorCode);
    }
}
