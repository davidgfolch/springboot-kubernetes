package com.dgf.controller;

import com.dgf.controller.model.ErrorResult;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.IBaseEntity;
import com.dgf.service.BusinessException;
import com.dgf.service.TranslationService;
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

    @Pointcut("within(com.dgf..*) && execution(com.dgf.controller.model.ResponseDTO com.dgf.*.controller.*.*(..))")
    @SuppressWarnings("unused")
    public void controllerMethods() { //pointcut
    }

    @Around("controllerMethods()")
    @SuppressWarnings("unused")
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
