// services/courseService.ts
import axios from 'axios';
import type { AxiosResponse } from 'axios';
import type {
    Course,
    Chapter,
    Lesson,
    Category,
    CourseCreateRequest,
    CourseUpdateRequest,
    ChapterCreateRequest,
    LessonCreateRequest,
    ApiResponse,
    PageResponse
} from '../types/course';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Instance Axios pour les endpoints admin
const adminAPI = axios.create({
    baseURL: `${API_BASE_URL}/admin/courses`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Instance Axios pour les endpoints publics
const publicAPI = axios.create({
    baseURL: `${API_BASE_URL}/courses/public`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Instance Axios pour les catégories
const categoryAPI = axios.create({
    baseURL: `${API_BASE_URL}/categories`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Intercepteur pour ajouter le token aux requêtes admin
adminAPI.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Intercepteur pour gérer les erreurs
[adminAPI, publicAPI, categoryAPI].forEach(api => {
    api.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response?.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/auth';
            }
            return Promise.reject(error);
        }
    );
});

export const courseService = {
    // ==================== GESTION ADMIN ====================

    async createCourse(data: CourseCreateRequest): Promise<ApiResponse<Course>> {
        console.log('🎓 Création d\'un nouveau cours:', data);
        const response: AxiosResponse<ApiResponse<Course>> = await adminAPI.post('', data);
        return response.data;
    },

    async updateCourse(courseId: string, data: CourseUpdateRequest): Promise<ApiResponse<Course>> {
        console.log('✏️ Mise à jour du cours:', courseId, data);
        const response: AxiosResponse<ApiResponse<Course>> = await adminAPI.put(`/${courseId}`, data);
        return response.data;
    },

    async uploadCourseImage(courseId: string, file: File): Promise<ApiResponse<Course>> {
        console.log('📷 Upload image pour le cours:', courseId);
        const formData = new FormData();
        formData.append('file', file);

        const response: AxiosResponse<ApiResponse<Course>> = await adminAPI.post(
            `/${courseId}/cover-image`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }
        );
        return response.data;
    },

    async addChapter(courseId: string, data: ChapterCreateRequest): Promise<ApiResponse<Chapter>> {
        console.log('📚 Ajout d\'un chapitre au cours:', courseId, data);
        const response: AxiosResponse<ApiResponse<Chapter>> = await adminAPI.post(
            `/${courseId}/chapters`,
            data
        );
        return response.data;
    },

    async addLesson(chapterId: string, data: LessonCreateRequest): Promise<ApiResponse<Lesson>> {
        console.log('📖 Ajout d\'une leçon au chapitre:', chapterId, data);
        const response: AxiosResponse<ApiResponse<Lesson>> = await adminAPI.post(
            `/chapters/${chapterId}/lessons`,
            data
        );
        return response.data;
    },

    async uploadLessonVideo(lessonId: string, file: File): Promise<ApiResponse<Lesson>> {
        console.log('🎥 Upload vidéo pour la leçon:', lessonId);
        const formData = new FormData();
        formData.append('file', file);

        const response: AxiosResponse<ApiResponse<Lesson>> = await adminAPI.post(
            `/lessons/${lessonId}/video`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }
        );
        return response.data;
    },

    async publishCourse(courseId: string): Promise<ApiResponse<Course>> {
        console.log('🚀 Publication du cours:', courseId);
        const response: AxiosResponse<ApiResponse<Course>> = await adminAPI.post(`/${courseId}/publish`);
        return response.data;
    },

    async getMyCourses(params?: {
        page?: number;
        size?: number;
        sortBy?: string;
        sortDir?: string;
    }): Promise<ApiResponse<PageResponse<Course>>> {
        const queryParams = new URLSearchParams();
        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.size !== undefined) queryParams.append('size', params.size.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortDir) queryParams.append('sortDir', params.sortDir);

        const response: AxiosResponse<ApiResponse<PageResponse<Course>>> = await adminAPI.get(
            `/my-courses?${queryParams.toString()}`
        );
        return response.data;
    },

    async getCourseForEditing(courseId: string): Promise<ApiResponse<Course>> {
        console.log('📖 Récupération du cours pour édition:', courseId);
        const response: AxiosResponse<ApiResponse<Course>> = await adminAPI.get(`/${courseId}/edit`);
        return response.data;
    },

    async deleteCourse(courseId: string): Promise<ApiResponse<void>> {
        console.log('🗑️ Suppression du cours:', courseId);
        const response: AxiosResponse<ApiResponse<void>> = await adminAPI.delete(`/${courseId}`);
        return response.data;
    },

    // ==================== ACCÈS PUBLIC ====================

    async getPublishedCourses(params?: {
        page?: number;
        size?: number;
        sortBy?: string;
        sortDir?: string;
        categoryId?: string;
        search?: string;
    }): Promise<ApiResponse<PageResponse<Course>>> {
        const queryParams = new URLSearchParams();
        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.size !== undefined) queryParams.append('size', params.size.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortDir) queryParams.append('sortDir', params.sortDir);
        if (params?.categoryId) queryParams.append('categoryId', params.categoryId);
        if (params?.search) queryParams.append('search', params.search);

        const response: AxiosResponse<ApiResponse<PageResponse<Course>>> = await publicAPI.get(
            `/?${queryParams.toString()}`
        );
        return response.data;
    },

    async getPublishedCourseById(courseId: string): Promise<ApiResponse<Course>> {
        const response: AxiosResponse<ApiResponse<Course>> = await publicAPI.get(`/${courseId}`);
        return response.data;
    },

    async getPopularCourses(limit: number = 6): Promise<ApiResponse<Course[]>> {
        const response: AxiosResponse<ApiResponse<Course[]>> = await publicAPI.get(
            `/popular?limit=${limit}`
        );
        return response.data;
    },

    async getTopRatedCourses(limit: number = 6): Promise<ApiResponse<Course[]>> {
        const response: AxiosResponse<ApiResponse<Course[]>> = await publicAPI.get(
            `/top-rated?limit=${limit}`
        );
        return response.data;
    },

    async getLatestCourses(limit: number = 6): Promise<ApiResponse<Course[]>> {
        const response: AxiosResponse<ApiResponse<Course[]>> = await publicAPI.get(
            `/latest?limit=${limit}`
        );
        return response.data;
    },

    // ==================== CATÉGORIES ====================

    async getCategories(): Promise<ApiResponse<Category[]>> {
        const response: AxiosResponse<ApiResponse<Category[]>> = await categoryAPI.get('');
        return response.data;
    },

    // ==================== UTILITAIRES ====================

    formatDuration(duration: string): string {
        // Formatage de la durée pour l'affichage
        return duration;
    },

    getStatusLabel(status: string): string {
        const labels = {
            DRAFT: 'Brouillon',
            PUBLISHED: 'Publié',
            ARCHIVED: 'Archivé'
        };
        return labels[status as keyof typeof labels] || status;
    },

    getLevelLabel(level: string): string {
        const labels = {
            BEGINNER: 'Débutant',
            INTERMEDIATE: 'Intermédiaire',
            ADVANCED: 'Avancé'
        };
        return labels[level as keyof typeof labels] || level;
    },

    getContentTypeLabel(type: string): string {
        const labels = {
            TEXT: 'Texte',
            VIDEO: 'Vidéo',
            DOCUMENT: 'Document'
        };
        return labels[type as keyof typeof labels] || type;
    }
};