package com.dgf.service;

import com.dgf.controller.model.ErrorResult;
import com.dgf.controller.model.ResponseDTO;
import com.dgf.model.IBaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.dgf.model.Constants.PARAMS_SEPARATOR;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final ResourceBundleMessageSource messageSource;

    public String translate(String code) {
        if (code==null) return null;
        if (code.contains(PARAMS_SEPARATOR)) {
            int keyCut = code.indexOf(PARAMS_SEPARATOR);
            Object[] args = code.substring(keyCut +PARAMS_SEPARATOR.length()).split(Pattern.quote(PARAMS_SEPARATOR));
            String codeCut= code.substring(0,keyCut);
            return translate(codeCut,args);
        }
        return translate(code,null);
    }

    public String removeParameters(String code) {
        if (code==null) return null;
        if (code.contains(PARAMS_SEPARATOR)) {
            int keyCut = code.indexOf(PARAMS_SEPARATOR);
            return code.substring(0,keyCut);
        }
        return code;
    }

    public String translate(String code, @Nullable Object[] args) {
        return messageSource.getMessage(code,args,"??"+code+"??",Locale.ENGLISH);
    }

    public <T extends IBaseEntity> ResponseDTO<T> translate(ResponseDTO<T> responseDTO) {
        ErrorResult error = responseDTO.getError();
        if (error !=null && StringUtils.hasText(error.getCode())) {
            error.setMessage(translate(error.getCode()));
        }
        return responseDTO;
    }

}
