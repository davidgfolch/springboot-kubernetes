package com.dgf.service;

import com.dgf.model.EntityConstraint;
import com.dgf.model.EntityFieldConstraint;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.Callable;

@Slf4j
public class BaseService {

    private BaseService() {}

    public static <T> T checkDataIntegrityViolationException(Callable<T> call, EntityConstraint constraints) {
        try {
            return call.call();
        } catch (DataIntegrityViolationException e) {
            return getDataIntegrityViolationInfo(constraints, e);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    private static <T> T getDataIntegrityViolationInfo(EntityConstraint constraints, DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            Throwable cause = e.getCause();
            if (cause.getCause() instanceof SQLIntegrityConstraintViolationException) {
                SQLIntegrityConstraintViolationException cause1 = (SQLIntegrityConstraintViolationException) cause.getCause();
                for (EntityFieldConstraint constraint : constraints.getFieldConstraints()) {
                    String msg = cause1.getMessage().toUpperCase();
                    if (msg.contains(constraints.getTableName().toUpperCase()) //contains table name
                            && msg.contains(constraint.getConstraintName().toUpperCase())) { //contains constraint name
                        throw new BusinessException(constraint.getCode());
                    }
                }
            }
        }
        throw e;
    }

}
