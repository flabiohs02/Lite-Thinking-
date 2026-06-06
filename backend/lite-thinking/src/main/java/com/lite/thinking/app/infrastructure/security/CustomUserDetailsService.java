package com.lite.thinking.app.infrastructure.security;

import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByIdentification(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con identificación: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getIdentification(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()))
        );
    }
}
