package com.smartRestaurant.validation;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidateUsername implements ValidationStrategy {
	private final static String VALID_USERNAME_REGEX = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";

	@Override
	public boolean validate(String s) {
		return StringUtils.isNotBlank(s)
				&& Pattern.compile(VALID_USERNAME_REGEX, Pattern.CASE_INSENSITIVE).matcher(s).matches();
	}

}
