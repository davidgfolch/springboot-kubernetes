package com.kubikdata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class User implements IBaseEntity {

    /**
     * <a href="https://stackoverflow.com/questions/3802192/regexp-java-for-password-validation">From stackoverflow check Jan Goyvaerts response</a>
     * <table>
     * <tr><td>^                 </td><td>start-of-string </td></tr>
     * <tr><td>(?=.*[0-9])       </td><td>a digit must occur at least once </td></tr>
     * <tr><td>(?=.*[a-z])       </td><td>a lower case letter must occur at least once </td></tr>
     * <tr><td>(?=.*[A-Z])       </td><td>an upper case letter must occur at least once </td></tr>
     * <tr><td>(?=.*[@#$%^&+=])  </td><td>a special character must occur at least once </td></tr>
     * <tr><td>(?=\S+$)          </td><td>no whitespace allowed in the entire string </td></tr>
     * <tr><td>.{8,}             </td><td>anything, at least eight places though </td></tr>
     * <tr><td>$                 </td><td>end-of-string </td></tr>
     * </table>
     */
    public static final String PASSWORD_SPECIAL_CHARS = "!|\"@ºª·#$~%&()?'¿¡€,.:;^/*+-=\\\\_";
    public static final String PASSWORD_REGEXP = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[" + PASSWORD_SPECIAL_CHARS + "])(?=\\S+$).{8,}$";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @NotBlank @Size(min=5)
    @Column(unique = true)
    String userName;
    @Email
    @Column(unique = true)
    String email;
    @NotBlank @Size(min=3)
    String name;
    @NotBlank @Size(min=3)
    String surname;
    @NotBlank @Size(min=8) @Pattern(regexp = PASSWORD_REGEXP)
    String pass;
    boolean verified=false;

}
