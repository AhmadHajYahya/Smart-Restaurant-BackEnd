package com.smartRestaurant.validation;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatePassword implements ValidationStrategy {
	private final static String VALID_PASSWORD_REGEX = "^(?=.*\\d)[A-Za-z0-9]{8,20}$";

	@Override
	public boolean validate(String s) {
		return StringUtils.isNotBlank(s)
				&& Pattern.compile(VALID_PASSWORD_REGEX, Pattern.CASE_INSENSITIVE).matcher(s).matches();
	}

}
