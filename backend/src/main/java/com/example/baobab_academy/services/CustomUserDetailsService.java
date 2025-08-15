package com.example.baobab_academy.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baobab_academy.models.User;
import com.example.baobab_academy.repositories.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Tentative de chargement de l'utilisateur avec l'email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec l'email: {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        log.debug("Utilisateur trouvé: {} avec le rôle: {}", user.getEmail(), user.getRole());
        return user;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
        log.debug("Tentative de chargement de l'utilisateur avec l'ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec l'ID: {}", id);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        return user;
    }
}
