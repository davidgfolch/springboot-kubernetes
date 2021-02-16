package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.service.BusinessException;
import com.kubikdata.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
public class BaseController {

    private final TranslateService translateService;

    public ResponseDTO exceptionHandling(Callable<ResponseDTO> call) {
        try {
            return call.call();
        } catch (BusinessException e) {
            return translateService.translate(new ResponseDTO(new ErrorResult(e)));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
