package com.dgf.controller;

import com.dgf.controller.model.ErrorResult;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.IBaseEntity;
import com.dgf.service.BusinessException;
import com.dgf.service.TranslationService;
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
