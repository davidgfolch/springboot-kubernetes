package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.model.IBaseEntity;
import com.kubikdata.service.BusinessException;
import com.kubikdata.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TranslationAOP {

    private final TranslationService translationService;

    @Pointcut("within(com.kubikdata..*) && execution(com.kubikdata.controller.model.ResponseDTO com.kubikdata.*.controller.*.*(..))")
    public void controllerMethods() { //pointcut
    }

    @Around("controllerMethods()")
    public <T extends IBaseEntity> ResponseDTO<T> translate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            @SuppressWarnings("unchecked")
            ResponseDTO<T> response = (ResponseDTO<T>) joinPoint.proceed();
            log.info("TRANSLATING: {}", response);
            ResponseDTO<T> translatedResponse = translationService.translate(response);
            log.info("TRANSLATED: {}", translatedResponse);
            return translatedResponse;
        } catch (BusinessException e) {
            log.info("TRANSLATING exception {}", e.getMessage());
            return new ResponseDTO<>(new ErrorResult(e, e.getMessage(), translationService.translate(e.getMessage())));
        }
    }
}
