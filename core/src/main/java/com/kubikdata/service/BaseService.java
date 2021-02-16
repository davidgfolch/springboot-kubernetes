package com.kubikdata.service;

import com.kubikdata.model.EntityConstraint;
import com.kubikdata.model.EntityFieldConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {

    public <T> T checkConstraintException(Callable<T> call, EntityConstraint constraints) {
        try {
            return call.call();
        } catch (DataIntegrityViolationException e) {
            return checkConstraintViolationException(constraints, e);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    private <T> T checkConstraintViolationException(EntityConstraint constraints, DataIntegrityViolationException e) {
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
