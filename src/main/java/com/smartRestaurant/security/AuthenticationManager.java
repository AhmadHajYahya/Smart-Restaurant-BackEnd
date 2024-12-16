package com.smartRestaurant.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

	private final UserDetailsServiceImpl userDetailsService;
	private JwtUtil jwtUtil;

	public AuthenticationManager(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String token = authentication.getCredentials().toString();

		String phoneNumber;
		try {
			phoneNumber = JwtUtil.getPhoneNumberFromToken(token);
		} catch (Exception e) {
			phoneNumber = null;
		}

		if (phoneNumber != null && jwtUtil.verifyToken(token)) {
			return userDetailsService.findByPhoneNumber(phoneNumber)
					.map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities()))
					.cast(Authentication.class);
		} else {
			return Mono.error(new UsernameNotFoundException("Invalid Credentials!"));
		}
	}
}
