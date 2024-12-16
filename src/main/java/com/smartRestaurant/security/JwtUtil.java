package com.smartRestaurant.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
	
	@Value("${token.secret.key}")
    private String secretKey; 

    public String generateToken(String phoneNumber) {
        return JWT.create()
            .withSubject(phoneNumber)
            .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))  // 30 minutes expiration
            .sign(Algorithm.HMAC256(secretKey));
    }

    @SuppressWarnings("unused")
	public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public static String getPhoneNumberFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }
}
