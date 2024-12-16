package com.smartRestaurant.passwordHashing;

import org.mindrot.jbcrypt.BCrypt;

/*
 * Utility class for hashing and verifying passwords using the BCrypt hashing
 * algorithm. BCrypt is a popular cryptographic hashing algorithm for securely
 * hashing passwords.
 */
public class PasswordUtils {

	// Hashes a plain text password using BCrypt with a generated salt.
	public static String hashPassword(String password) {
		String salt = BCrypt.gensalt(12);
		return BCrypt.hashpw(password, salt);
	}

	// Verifies a plain text candidate password against a previously hashed
	// password.
	public static boolean verifyPassword(String candidatePassword, String hashedPassword) {
		return BCrypt.checkpw(candidatePassword, hashedPassword);
	}
}
