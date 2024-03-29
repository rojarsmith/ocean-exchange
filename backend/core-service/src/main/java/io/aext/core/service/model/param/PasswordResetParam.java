package io.aext.core.service.model.param;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

/**
 * @author rojar
 *
 * @date 2021-06-29
 */
@Data
public class PasswordResetParam {
	@NotBlank(message = "{Register.username.null}")
	@Length(min = 3, max = 20, message = "{Register.username.length}")
	private String username;

	@NotBlank(message = "{Register.password.null}")
	@Length(min = 6, max = 20, message = "{Register.password.length}")
	private String password;

	@NotBlank(message = "{Register.verify.null}")
	private String token;
}
