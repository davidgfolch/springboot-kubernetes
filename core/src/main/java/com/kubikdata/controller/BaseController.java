package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.model.IBaseEntity;
import com.kubikdata.service.BusinessException;
import com.kubikdata.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
public class BaseController<T extends IBaseEntity> {

    private final TranslationService translationService;

    public ResponseDTO<T> exceptionHandling(Callable<ResponseDTO<T>> call) {
        try {
            return call.call();
        } catch (BusinessException e) {
            return translationService.translate(new ResponseDTO<>(new ErrorResult(e)));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
