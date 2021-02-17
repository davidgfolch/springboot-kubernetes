package com.kubikdata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Arrays;

import static com.kubikdata.model.Constants.PARAMS_SEPARATOR;
import static com.kubikdata.model.Constants.VALIDATION_MIN_SIZE;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = User.UK_USER_NAME, columnNames = {"user_name"}),
                @UniqueConstraint(name = User.UK_EMAIL, columnNames = {"email"})
        })

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class User implements IBaseEntity {

    private static final String TABLE_NAME = "user";
    public static final String UK_USER_NAME = "UK_userName";
    public static final String UK_EMAIL = "UK_email";
    public static final String VALIDATION_USER_USER_NAME = "validation.user.userName";
    public static final String VALIDATION_USER_EMAIL = "validation.user.email";
    public static final EntityConstraint CONSTRAINTS = new EntityConstraint(TABLE_NAME,
            Arrays.asList(
                    EntityFieldConstraint.create(UK_USER_NAME, VALIDATION_USER_USER_NAME),
                    EntityFieldConstraint.create(UK_EMAIL, VALIDATION_USER_EMAIL)
            )
    );
    /**
     * <a href="https://stackoverflow.com/questions/3802192/regexp-java-for-password-validation">From stackoverflow check my own response</a>
     */
    @SuppressWarnings({"regexp", "RegExpUnexpectedAnchor", "RegExpRedundantEscape"})
    public static final String PASSWORD_SPECIAL_CHARS = "@#$%^`<>&+=!ºª·#~%&'¿¡€,:;*/+-.=_\\\\\"\\[\\]\\(\\)\\|\\_\\?";
    public static final String PASSWORD_SPECIAL_CHARS_MESSAGE = "@#$%^`<>&+=!ºª·#~%&'¿¡€,:;*/+-.=_\"[]()|_?";  //unscaped for message
    public static final int PASSWORD_MIN_SIZE = 8;
    public static final String PASSWORD_REGEXP = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[" + PASSWORD_SPECIAL_CHARS + "])(?=\\S+$).{" + PASSWORD_MIN_SIZE + ",}$";
    public static final int PASS_MIN_SIZE = 8;
    public static final String VALIDATION_USER_PASSWORD = "validation.user.password";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull @NotBlank @Size(min = 5, message = VALIDATION_MIN_SIZE + PARAMS_SEPARATOR + "5")
    @Column(name = "user_name")
    String userName;
    @NotNull @Email(message = "validation.email")
    String email;
    @NotNull @NotBlank @Size(min = 3, message = VALIDATION_MIN_SIZE + PARAMS_SEPARATOR + "3")
    String name;
    @NotNull @NotBlank @Size(min = 3, message = VALIDATION_MIN_SIZE + PARAMS_SEPARATOR + "3")
    String surname;
    @NotNull @NotBlank @Size(min = PASS_MIN_SIZE, message = VALIDATION_MIN_SIZE + PARAMS_SEPARATOR + PASS_MIN_SIZE)
    @Pattern(regexp = PASSWORD_REGEXP, message = VALIDATION_USER_PASSWORD + PARAMS_SEPARATOR + PASSWORD_MIN_SIZE + PARAMS_SEPARATOR + PASSWORD_SPECIAL_CHARS_MESSAGE)
    //TODO Works as is with ValidationMessages.properties (JSR-303) but can't find the way to interpolate PASSWORD_MIN_SIZE & PASSWORD_SPECIAL_CHARS
    //https://www.logicbig.com/tutorials/java-ee-tutorial/bean-validation/validation-messages.html
    //@Pattern(regexp = PASSWORD_REGEXP, message = "{validation.user.password}") // + PASSWORD_MIN_SIZE + PARAMS_SEPARATOR + PASSWORD_SPECIAL_CHARS)
    String pass;
    boolean verified = false;

}
