package com.dgf.user.service;

import com.dgf.service.BusinessException;

public class UserBusinessException extends BusinessException {

    public UserBusinessException(String errorCode) {
        super(errorCode);
    }

}
