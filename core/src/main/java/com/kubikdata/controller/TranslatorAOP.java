package com.kubikdata.controller;

import com.kubikdata.controller.model.ErrorResult;
import com.kubikdata.controller.model.ResponseDTO;
import com.kubikdata.service.BusinessException;
import com.kubikdata.service.TranslateService;
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
public class TranslatorAOP {

    private final TranslateService translateService;

    @Pointcut("within(com.kubikdata..*) && execution(* com.kubikdata.*.controller.*.*(..))")
    public void controllerMethods() { //pointcut
    }

    @Around("controllerMethods()")
    public ResponseDTO translate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            ResponseDTO response = (ResponseDTO) joinPoint.proceed();
            log.info("TRANSLATING: {}", response);
            ResponseDTO translatedResponse = translateService.translate(response);
            log.info("TRANSLATED: {}", translatedResponse);
            return translatedResponse;
        } catch (BusinessException e) {
            log.info("Translating exception {}", e.getMessage());
            return new ResponseDTO(new ErrorResult(e, e.getMessage(), translateService.translate(e.getMessage())));
        }
    }
}
