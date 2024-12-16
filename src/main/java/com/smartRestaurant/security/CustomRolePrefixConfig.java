package com.smartRestaurant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class CustomRolePrefixConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public CustomRolePrefixConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        // Uses the existing UserDetailsServiceImpl to retrieve the user details.
        return phoneNumber -> userDetailsService.findByPhoneNumber(phoneNumber)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User Not Found"))))
                .map(userDetails -> new User(userDetails.getUsername(), userDetails.getPassword(), getAuthoritiesWithoutPrefix(userDetails.getAuthorities())));
    }

    private Collection<GrantedAuthority> getAuthoritiesWithoutPrefix(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))  // No prefix needed here
                .collect(Collectors.toList());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
