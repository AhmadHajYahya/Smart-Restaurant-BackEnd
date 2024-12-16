package com.smartRestaurant.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

	private final AuthenticationManager authenticationManager;

	public SecurityContextRepository(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Mono<Void> save(ServerWebExchange sweep, SecurityContext sc) {
		throw new UnsupportedOperationException("Save method not supported!");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange swe) {
	    ServerHttpRequest request = swe.getRequest();
	    String authToken = request.getHeaders().getFirst("Authorization");

	    if (authToken != null && authToken.startsWith("Bearer ")) {
	        authToken = authToken.substring(7);
	    } else if (request.getCookies().get("AUTH-TOKEN") != null) {
	        authToken = request.getCookies().get("AUTH-TOKEN").get(0).getValue();
	    } else {
	        // Now check for token in query parameters
	        authToken = request.getQueryParams().getFirst("token");
	    }

	    if (authToken != null) {
	        Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
	        return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
	    }
	    return Mono.empty();
	}

}