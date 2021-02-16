package com.kubikdata.user.service;

import com.kubikdata.service.BusinessException;

public class UserBusinessException extends BusinessException {

    public UserBusinessException(String errorCode) {
        super(errorCode);
    }

}
