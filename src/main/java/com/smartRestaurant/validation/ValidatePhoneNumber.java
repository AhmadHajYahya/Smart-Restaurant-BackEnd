package com.smartRestaurant.validation;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatePhoneNumber implements ValidationStrategy{
	private final static String VALID_PHONE_NUMBER_REGEX = "^05[023489]{1}[0-9]{7}$";

	@Override
	public boolean validate(String s) {
		 return StringUtils.isNotBlank(s) && Pattern.compile(VALID_PHONE_NUMBER_REGEX, Pattern.CASE_INSENSITIVE).matcher(s).matches();
	}
}
