package com.example.baobab_academy.services;

import com.example.baobab_academy.dtos.UserResponse;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.models.enums.UserRole;
import com.example.baobab_academy.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MongoTemplate mongoTemplate;

    public Page<UserResponse> getAllUsers(Pageable pageable, String search) {
        Query query = new Query();

        // Ajouter la recherche si fournie
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                Criteria.where("firstName").regex(search, "i"),
                Criteria.where("lastName").regex(search, "i"),
                Criteria.where("email").regex(search, "i")
            );
            query.addCriteria(searchCriteria);
        }

        // Appliquer la pagination
        query.with(pageable);

        // Exécuter la requête
        List<User> users = mongoTemplate.find(query, User.class);

        // Convertir en UserResponse
        List<UserResponse> userResponses = users.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();

        // Compter le total pour la pagination
        long total = mongoTemplate.count(query.skip(0).limit(0), User.class);

        return PageableExecutionUtils.getPage(
                userResponses,
                pageable,
                () -> total
        );
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier que l'utilisateur n'est pas admin (optionnel)
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Impossible de supprimer un administrateur");
        }
        
        userRepository.delete(user);
        log.info("Utilisateur supprimé: {}", userId);
    }

    public UserResponse changeUserRole(String userId, String roleString) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        try {
            UserRole newRole = UserRole.valueOf(roleString.toUpperCase());
            user.setRole(newRole);
            User savedUser = userRepository.save(user);
            
            log.info("Rôle de l'utilisateur {} changé vers {}", userId, newRole);
            return modelMapper.map(savedUser, UserResponse.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide: " + roleString);
        }
    }

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Compter les utilisateurs par rôle
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findAllByRole(UserRole.ADMIN).size();
        long totalStudents = userRepository.findAllByRole(UserRole.USER).size();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalStudents", totalStudents);
        
        return stats;
    }
}