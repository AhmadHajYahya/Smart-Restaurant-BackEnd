package com.smartRestaurant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public SecurityConfig(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @SuppressWarnings("removal")
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers("/users/login","/users/register","/users/guest","/password-recovery/*","/whatsapp/send-code","/**").permitAll()
//            .pathMatchers("/smart-restaurant/admin/**").hasAuthority("ADMIN")
//            .pathMatchers("/whatsapp/send-receipt").hasAnyAuthority("REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/bookings/**").hasAnyAuthority("ADMIN", "REGISTERED_USER")
//            .pathMatchers("/real-time/update/**").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/call-waiter/create-call").hasAnyAuthority("REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/call-waiter/get-calls","/call-waiter/delete-call").hasAnyAuthority("WAITER")
//            .pathMatchers("/meals/**").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/mealOrders/all", "/mealOrders/update", "/mealOrders/prioritized-orders").hasAnyAuthority("ADMIN", "COOK", "WAITER")
//            .pathMatchers("/mealOrders/user-orders-history").hasAuthority("REGISTERED_USER")
//            .pathMatchers("/mealOrders/add").hasAnyAuthority("REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/menu").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/menu/**").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/notifications/send-notification").hasAuthority("ADMIN")
//            .pathMatchers("/notifications/stream", "/notifications/all").hasAnyAuthority("ADMIN", "REGISTERED_USER")
//            .pathMatchers("/payments/create-payment-intent").hasAnyAuthority("REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/receipts/create-receipt").hasAnyAuthority("REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/receipts/**").hasAuthority("ADMIN")
//            .pathMatchers("/tables/all","/tables/table").hasAnyAuthority("ADMIN", "COOK", "WAITER")
//            .pathMatchers("/tables/guests-load").hasAnyAuthority("ADMIN", "COOK", "WAITER","REGISTERED_USER")
//            .pathMatchers("/tables/take-table", "/tables/free-table").hasAnyAuthority("ADMIN", "COOK", "WAITER","REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/users/registered/update").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER")
//            .pathMatchers("/users/update/online").hasAnyAuthority("ADMIN", "COOK", "WAITER", "REGISTERED_USER", "GUEST_USER")
//            .pathMatchers("/users/delete/guest").hasAnyAuthority("ADMIN", "GUEST_USER")
//            .anyExchange().authenticated()
            .and()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository);

        return http.build();
    }

}
