package com.dgf.user.controller;

import com.dgf.model.IBaseEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.dgf.model.Constants.PARAMS_SEPARATOR;
import static com.dgf.model.Constants.VALIDATION_MIN_SIZE;

@Data
@Validated
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class VerifyDTO implements IBaseEntity {

    @NotNull
    @NotBlank
    @Size(min = 5, message = VALIDATION_MIN_SIZE + PARAMS_SEPARATOR + "5")
    @Column(name = "user_name")
    final String userName;

    @NotNull
    @NotBlank
    final String token;

}
