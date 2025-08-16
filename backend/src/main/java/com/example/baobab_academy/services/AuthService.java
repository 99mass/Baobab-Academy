package com.example.baobab_academy.services;

import com.example.baobab_academy.dtos.AuthResponse;
import com.example.baobab_academy.dtos.LoginRequest;
import com.example.baobab_academy.dtos.RegisterRequest;
import com.example.baobab_academy.dtos.UpdateProfileRequest;
import com.example.baobab_academy.dtos.UserResponse;
import com.example.baobab_academy.exceptions.EmailAlreadyExistsException;
import com.example.baobab_academy.exceptions.InvalidCredentialsException;
import com.example.baobab_academy.exceptions.PasswordMismatchException;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.models.enums.UserRole;
import com.example.baobab_academy.repositories.UserRepository;
import com.example.baobab_academy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Un compte avec cet email existe déjà");
        }

        // Vérifier la confirmation du mot de passe
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Les mots de passe ne correspondent pas");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.USER);

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: {}", savedUser.getEmail());

        // Authentifier automatiquement l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Générer le token JWT
        String token = tokenProvider.generateToken(authentication);

        // Créer la réponse
        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour l'email: {}", request.getEmail());

        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            // Récupérer l'utilisateur
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Email ou mot de passe incorrect"));

            // Générer le token JWT
            String token = tokenProvider.generateToken(authentication);

            log.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());

            // Créer la réponse
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return new AuthResponse(token, userResponse);

        } catch (Exception e) {
            log.error("Échec de la connexion pour l'email: {}", request.getEmail());
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
            }
        }

        // Mettre à jour les informations
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Sauvegarder
        User updatedUser = userRepository.save(user);

        log.info("Profil mis à jour pour l'utilisateur: {}", updatedUser.getEmail());

        return convertToUserResponse(updatedUser);
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());

        log.debug("Conversion User vers UserResponse - ID: {}, Email: {}, Rôle: {}",
                user.getId(), user.getEmail(), user.getRole());

        return userResponse;
    }
}