package com.smartRestaurant.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Mono<UserDetails> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                	if(user.getRole().equals(Roles.GUEST_USER)) {
                		return User.withUsername(user.getUsername())
                				.password("")
                        .authorities(user.getRole().name())
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();
                	}else {
                		return User.withUsername(user.getUsername())
                        
                        .password(user.getPassword())
                        .authorities(user.getRole().name())
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();
                	}
                	})
                
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User Not Found"))));
    }
}
