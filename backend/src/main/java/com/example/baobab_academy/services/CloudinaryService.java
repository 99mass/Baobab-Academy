package com.example.baobab_academy.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
        log.info("✅ Service Cloudinary initialisé");
    }

    /**
     * Upload une image de couverture de cours
     */
    public CloudinaryUploadResult uploadCourseImage(MultipartFile file, String courseId) throws IOException {
        validateImageFile(file);
        
        String publicId = generateCourseImagePublicId(courseId);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "baobab-academy/courses",
                "transformation", "w_800,h_600,c_fill,q_auto:good",
                "format", "webp"
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        
        log.info("✅ Image de cours uploadée: {}", result.get("secure_url"));
        
        return CloudinaryUploadResult.builder()
                .publicId((String) result.get("public_id"))
                .url((String) result.get("url"))
                .secureUrl((String) result.get("secure_url"))
                .format((String) result.get("format"))
                .width((Integer) result.get("width"))
                .height((Integer) result.get("height"))
                .bytes((Integer) result.get("bytes"))
                .success(true)
                .build();
    }

    /**
     * Upload une vidéo pour une leçon
     */
    public CloudinaryUploadResult uploadLessonVideo(MultipartFile file, String courseId, String lessonId) throws IOException {
        validateVideoFile(file);
        
        String publicId = generateLessonVideoPublicId(courseId, lessonId);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "baobab-academy/lessons/videos",
                "resource_type", "video",
                "transformation", "q_auto:good,f_auto"
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        
        log.info("✅ Vidéo de leçon uploadée: {}", result.get("secure_url"));
        
        return CloudinaryUploadResult.builder()
                .publicId((String) result.get("public_id"))
                .url((String) result.get("url"))
                .secureUrl((String) result.get("secure_url"))
                .format((String) result.get("format"))
                .width((Integer) result.get("width"))
                .height((Integer) result.get("height"))
                .bytes((Integer) result.get("bytes"))
                .duration((Double) result.get("duration"))
                .success(true)
                .build();
    }

    /**
     * Upload un document pour une leçon - SOLUTION ROBUSTE
     */
    public CloudinaryUploadResult uploadLessonDocument(MultipartFile file, String courseId, String lessonId) throws IOException {
        validateDocumentFile(file);
        
        String originalFilename = file.getOriginalFilename();
        String publicId = generateLessonDocumentPublicId(courseId, lessonId, originalFilename);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "baobab-academy/lessons/documents",
                "resource_type", "raw"
                // 🆕 ON ENLÈVE use_filename et unique_filename pour avoir plus de contrôle
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        
        log.info("✅ Document de leçon uploadé: {}", result.get("secure_url"));
        
        return CloudinaryUploadResult.builder()
                .publicId((String) result.get("public_id"))
                .url((String) result.get("url"))
                .secureUrl((String) result.get("secure_url"))
                .format((String) result.get("format"))
                .bytes((Integer) result.get("bytes"))
                .success(true)
                .build();
    }

    /**
     * 🆕 MÉTHODE CORRIGÉE pour générer un publicId avec extension
     */
    private String generateLessonDocumentPublicId(String courseId, String lessonId, String originalFilename) {
        // Extraire l'extension et le nom
        String extension = "";
        String baseName = "document";
        
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex); // Garde le point
                baseName = originalFilename.substring(0, lastDotIndex);
            } else {
                baseName = originalFilename;
            }
            
            // Nettoyer le nom de base
            baseName = baseName
                .replaceAll("[^a-zA-Z0-9_-]", "_")
                .replaceAll("_{2,}", "_");
                
            // Limiter la longueur
            if (baseName.length() > 30) {
                baseName = baseName.substring(0, 30);
            }
        }
        
        // 🎯 IMPORTANT : Le publicId DOIT inclure l'extension pour les fichiers raw
        return String.format("%s_%s_%d%s", 
            baseName, 
            lessonId.substring(0, Math.min(8, lessonId.length())), // 8 premiers chars de l'ID
            System.currentTimeMillis(),
            extension // 🔑 Extension incluse ici
        );
    }

    /**
     * Supprime une ressource de Cloudinary
     */
    public void deleteResource(String publicId, String resourceType) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> deleteOptions = ObjectUtils.asMap("resource_type", resourceType);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, deleteOptions);
            
            String deleteResult = (String) result.get("result");
            if ("ok".equals(deleteResult)) {
                log.info("✅ Ressource supprimée: {}", publicId);
            } else {
                log.warn("⚠️ Ressource non trouvée ou déjà supprimée: {}", publicId);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de la ressource {}: {}", publicId, e.getMessage());
        }
    }

    /**
     * Supprime une image
     */
    public void deleteImage(String publicId) {
        deleteResource(publicId, "image");
    }

    /**
     * Supprime une vidéo
     */
    public void deleteVideo(String publicId) {
        deleteResource(publicId, "video");
    }

    /**
     * Supprime un document
     */
    public void deleteDocument(String publicId) {
        deleteResource(publicId, "raw");
    }

    /**
     * Extrait le publicId d'une URL Cloudinary
     */
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // URL exemple: https://res.cloudinary.com/dnbyf2jqg/image/upload/v1234567890/baobab-academy/courses/course-id/image.webp
            String[] parts = url.split("/");
            
            // Trouver l'index après 'upload'
            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i])) {
                    uploadIndex = i;
                    break;
                }
            }
            
            if (uploadIndex == -1 || uploadIndex + 2 >= parts.length) {
                return null;
            }
            
            // Ignorer la version (commence par 'v')
            int startIndex = uploadIndex + 1;
            if (parts[uploadIndex + 1].startsWith("v")) {
                startIndex = uploadIndex + 2;
            }
            
            // Reconstruire le publicId sans l'extension
            StringBuilder publicId = new StringBuilder();
            for (int i = startIndex; i < parts.length; i++) {
                if (i > startIndex) {
                    publicId.append("/");
                }
                String part = parts[i];
                // Retirer l'extension du dernier élément
                if (i == parts.length - 1 && part.contains(".")) {
                    part = part.substring(0, part.lastIndexOf("."));
                }
                publicId.append(part);
            }
            
            return publicId.toString();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du publicId de l'URL {}: {}", url, e.getMessage());
            return null;
        }
    }

    // Méthodes de validation
    private void validateImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IOException("Le fichier image est trop volumineux (max 5MB)");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nom de fichier invalide");
        }

        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
        String extension = fileName.toLowerCase().substring(fileName.lastIndexOf("."));
        
        if (!allowedExtensions.contains(extension)) {
            throw new IOException("Format d'image non supporté. Utilisez: JPG, PNG, GIF, WebP");
        }
    }

    private void validateVideoFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            throw new IOException("Le fichier vidéo est trop volumineux (max 100MB)");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nom de fichier invalide");
        }

        List<String> allowedExtensions = Arrays.asList(".mp4", ".mov", ".avi", ".mkv", ".webm");
        String extension = fileName.toLowerCase().substring(fileName.lastIndexOf("."));
        
        if (!allowedExtensions.contains(extension)) {
            throw new IOException("Format vidéo non supporté. Utilisez: MP4, MOV, AVI, MKV, WebM");
        }
    }

    private void validateDocumentFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IOException("Le fichier document est trop volumineux (max 10MB)");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nom de fichier invalide");
        }

        List<String> allowedExtensions = Arrays.asList(".pdf", ".doc", ".docx", ".ppt", ".pptx", ".txt");
        String extension = fileName.toLowerCase().substring(fileName.lastIndexOf("."));
        
        if (!allowedExtensions.contains(extension)) {
            throw new IOException("Format de document non supporté. Utilisez: PDF, DOC, DOCX, PPT, PPTX, TXT");
        }
    }

    // Méthodes de génération de publicId
    private String generateCourseImagePublicId(String courseId) {
        return String.format("courses/%s/cover_%d", courseId, System.currentTimeMillis());
    }

    private String generateLessonVideoPublicId(String courseId, String lessonId) {
        return String.format("lessons/%s/%s/video_%d", courseId, lessonId, System.currentTimeMillis());
    }


    // Classe interne pour le résultat d'upload
    @lombok.Data
    @lombok.Builder
    public static class CloudinaryUploadResult {
        private String publicId;
        private String url;
        private String secureUrl;
        private String format;
        private Integer width;
        private Integer height;
        private Integer bytes;
        private Double duration; // Pour les vidéos
        private boolean success;
    }
}