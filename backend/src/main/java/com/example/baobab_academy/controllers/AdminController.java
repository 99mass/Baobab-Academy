package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.UserResponse;
import com.example.baobab_academy.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "Endpoints d'administration")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Récupérer la liste des utilisateurs")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {

        log.info("Récupération de la liste des utilisateurs - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                page, size, sortBy, sortDir, search);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users = adminService.getAllUsers(pageable, search);

        return ResponseEntity.ok(ApiResponse.success("Liste des utilisateurs récupérée avec succès", users));
    }

    @Operation(summary = "Supprimer un utilisateur")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String userId) {
        log.info("Suppression de l'utilisateur avec l'ID: {}", userId);
        
        adminService.deleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès"));
    }

    @Operation(summary = "Changer le rôle d'un utilisateur")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable String userId,
            @RequestParam String role) {
        
        log.info("Changement du rôle de l'utilisateur {} vers {}", userId, role);
        
        UserResponse updatedUser = adminService.changeUserRole(userId, role);
        
        return ResponseEntity.ok(ApiResponse.success("Rôle modifié avec succès", updatedUser));
    }

    @Operation(summary = "Obtenir les statistiques de la plateforme")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getPlatformStats() {
        log.info("Récupération des statistiques de la plateforme");
        
        Object stats = adminService.getPlatformStats();
        
        return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées avec succès", stats));
    }
}