package com.dgf.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class EntityConstraint {

    private final String tableName;
    private final List<EntityFieldConstraint> fieldConstraints;

}
