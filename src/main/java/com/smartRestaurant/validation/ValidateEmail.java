package com.smartRestaurant.validation;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidateEmail implements ValidationStrategy {
	private final static String VALID_EMAIL_REGEX = "^[a-zA-Z0-9!#$%&*+\\/=?^_{|}~-][a-zAZ0-9.!#$%&*+\\/=?^_{|}~-]{0,63}@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]{2,7})*$";

	@Override
	public boolean validate(String s) {
		return StringUtils.isNotBlank(s)
				&& Pattern.compile(VALID_EMAIL_REGEX, Pattern.CASE_INSENSITIVE).matcher(s).matches();
	}
}
