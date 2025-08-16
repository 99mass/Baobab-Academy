package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.AuthResponse;
import com.example.baobab_academy.dtos.LoginRequest;
import com.example.baobab_academy.dtos.RegisterRequest;
import com.example.baobab_academy.dtos.UpdateProfileRequest;
import com.example.baobab_academy.dtos.UserResponse;
import com.example.baobab_academy.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints d'authentification")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Demande d'inscription reçue pour l'email: {}", request.getEmail());

        AuthResponse authResponse = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inscription réussie", authResponse));
    }

    @Operation(summary = "Connexion d'un utilisateur")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Demande de connexion reçue pour l'email: {}", request.getEmail());

        AuthResponse authResponse = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", authResponse));
    }

    @Operation(summary = "Récupérer les informations de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        log.info("Récupération du profil pour l'utilisateur: {}", email);

        UserResponse userResponse = authService.getCurrentUser(email);

        return ResponseEntity.ok(ApiResponse.success("Profil récupéré avec succès", userResponse));
    }

    @Operation(summary = "Déconnexion d'un utilisateur")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "unknown";
        log.info("Déconnexion de l'utilisateur: {}", email);

        // Note: Avec JWT, la déconnexion côté serveur est limitée
        // Le client doit simplement supprimer le token
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie"));
    }

    @Operation(summary = "Vérifier la validité du token")
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.success("Token valide"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token invalide"));
        }
    }

    @Operation(summary = "Mettre à jour le profil de l'utilisateur")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        log.info("Mise à jour du profil pour l'utilisateur: {}", currentEmail);

        UserResponse updatedUser = authService.updateProfile(currentEmail, request);

        return ResponseEntity.ok(ApiResponse.success("Profil mis à jour avec succès", updatedUser));
    }
}
