import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Save, 
  Upload, 
  Plus, 
  Eye, 
  ArrowLeft, 
  BookOpen, 
  Video, 
  FileText,
  Settings,
  Trash2,
  GripVertical,
  Edit3,
  Send,
} from 'lucide-react';
import { courseService } from '../services/courseService';
import type { 
  Course, 
  Category, 
  Chapter, 
  Lesson,
  CourseCreateRequest, 
  CourseUpdateRequest,
  CourseLevel,
  ContentType} from '../types/course';

export default function CourseEditor() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [course, setCourse] = useState<Partial<Course>>({
    title: '',
    description: '',
    imageUrl: '',
    price: 0,
    isPublished: false,
    category: '',
  });
  const [lessons, setLessons] = useState<Partial<Lesson>[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [activeTab, setActiveTab] = useState<'basic' | 'content' | 'settings'>('basic');

  // État du formulaire de base
  const [basicForm, setBasicForm] = useState({
    title: '',
    description: '',
    categoryId: '',
    level: 'DEBUTANT' as CourseLevel,
    duration: ''
  });

  // État pour l l'upload d image
  const [imageUploading, setImageUploading] = useState(false);
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string>('');

  // État pour la durée
  const [duration, setDuration] = useState({ hours: '', minutes: '' });

  // État pour les messages d'erreur
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // État pour les chapitres et leçons
  const [chapters, setChapters] = useState<Chapter[]>([]);
  const [expandedChapter, setExpandedChapter] = useState<string | null>(null);

  // États des modales
  const [showChapterModal, setShowChapterModal] = useState(false);
  const [showLessonModal, setShowLessonModal] = useState(false);
  const [selectedChapterId, setSelectedChapterId] = useState<string>('');

  // Formulaires des modales
  const [chapterForm, setChapterForm] = useState({ title: '' });
  const [lessonForm, setLessonForm] = useState({
    title: '',
    content: '',
    contentType: 'TEXT' as ContentType,
    videoUrl: ''
  });

  // Chargement initial
  useEffect(() => {
    loadCategories();
    if (id) {
      loadCourse();
    }
  }, [id]);

  const loadCategories = async () => {
    try {
      const response = await courseService.getCategories();
      if (response.success && response.data) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Erreur lors du chargement des catégories:', error);
    }
  };

  const loadCourse = async () => {
    if (!id) return;

    setLoading(true);
    try {
      const response = await courseService.getCourseForEditing(id);
      if (response.success && response.data) {
        const courseData = response.data;
        setCourse(courseData);
        setBasicForm({
          title: courseData.title,
          description: courseData.description,
          categoryId: courseData.categoryId || '',
          level: courseData.level,
          duration: courseData.duration
        });
        if (courseData.duration) {
            const parsed = parseDurationString(courseData.duration);
            setDuration({ hours: parsed.hours, minutes: parsed.minutes });
        }
        setImagePreview(courseData.coverImage || '');
        setChapters(courseData.chapters || []);
      }
    } catch (error) {
      console.error('Erreur lors du chargement du cours:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveBasicInfo = async () => {
    setSaving(true);
    setErrorMessage(null); // Clear previous errors
    try {
      const formattedDurationString = formatDuration(duration.hours, duration.minutes);

      if (id) {
        // Mise à jour
        const updateData: CourseUpdateRequest = {
          ...basicForm,
          duration: formattedDurationString
        };
        const response = await courseService.updateCourse(id, updateData);
        if (response.success && response.data) {
          setCourse(response.data);
          // alert('Cours mis à jour avec succès !'); // Remove alert
        }
      } else {
        // Création
        const createData: CourseCreateRequest = {
          ...basicForm,
          duration: formattedDurationString
        };
        const response = await courseService.createCourse(createData);
        if (response.success && response.data) {
          const newCourse = response.data;
          if (selectedImage) {
            // Si une image est sélectionnée, l'uploader avant de naviguer
            await handleImageUpload(newCourse.id);
          } else {
            setCourse(newCourse);
            navigate(`/admin/course/edit/${newCourse.id}`, { replace: true });
            // alert('Cours créé avec succès !'); // Remove alert
          }
        }
      }
    } catch (error: any) {
      console.error('Erreur lors de la sauvegarde:', error);
      console.log('Backend error response data:', error.response?.data);
      let errorMessageText = 'Erreur lors de la sauvegarde';

      if (error.response && error.response.data) {
        const responseData = error.response.data; // This is the outer object {success, message, data, timestamp}

        if (responseData.message) { // Use the generic message first if available
            errorMessageText = responseData.message;
        }

        if (responseData.data && typeof responseData.data === 'object') {
            // Now, responseData.data is the object containing field-specific errors
            const fieldErrors = Object.values(responseData.data).filter(msg => typeof msg === 'string');
            if (fieldErrors.length > 0) {
                errorMessageText = fieldErrors.join('; '); // Join multiple field error messages
            }
        } else if (typeof responseData === 'string') { // Fallback if the whole response data is a string
            errorMessageText = responseData;
        }
      }
      setErrorMessage(errorMessageText);
    } finally {
      setSaving(false);
    }
  };

  const handleImageUpload = async (courseId: string) => {
    if (!selectedImage) return;

    setImageUploading(true);
    try {
      const response = await courseService.uploadCourseImage(courseId, selectedImage);
      if (response.success && response.data) {
        // Si c'est une création, on navigue après l'upload
        if (!id) {
          setCourse(response.data);
          navigate(`/admin/course/edit/${response.data.id}`, { replace: true });
          alert('Cours créé et image uploadée avec succès !');
        } else {
          setCourse(response.data);
          alert('Image uploadée avec succès !');
        }
        setImagePreview(response.data.coverImage || '');
        setSelectedImage(null);
      }
    } catch (error: any) {
      console.error('Erreur lors de l\'upload:', error);
      alert(error.response?.data?.message || 'Erreur lors de l\'upload');
    } finally {
      setImageUploading(false);
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedImage(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setImagePreview(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAddChapter = async () => {
    if (!course?.id || !chapterForm.title.trim()) return;

    try {
      const response = await courseService.addChapter(course.id, {
        title: chapterForm.title.trim(),
        orderIndex: chapters.length + 1
      });
      if (response.success && response.data) {
        const newChapter: Chapter = {
          ...response.data,
          lessons: []
        };
        setChapters(prev => [...prev, newChapter]);
        setChapterForm({ title: '' });
        setShowChapterModal(false);
        alert('Chapitre ajouté avec succès !');
      }
    } catch (error: any) {
      console.error('Erreur lors de l\'ajout du chapitre:', error);
      alert(error.response?.data?.message || 'Erreur lors de l\'ajout');
    }
  };

  const handleAddLesson = async () => {
    if (!selectedChapterId || !lessonForm.title.trim()) return;

    const currentChapter = chapters.find(c => c.id === selectedChapterId);
    const orderIndex = (currentChapter?.lessons?.length || 0) + 1;

    try {
      const response = await courseService.addLesson(selectedChapterId, {
        title: lessonForm.title.trim(),
        content: lessonForm.content,
        contentType: lessonForm.contentType,
        videoUrl: lessonForm.videoUrl || undefined,
        orderIndex: orderIndex
      });
      if (response.success && response.data) {
        setChapters(prev => prev.map(chapter => 
          chapter.id === selectedChapterId 
            ? { ...chapter, lessons: [...(chapter.lessons || []), response.data!] }
            : chapter
        ));
        setLessonForm({
          title: '',
          content: '',
          contentType: 'TEXT',
          videoUrl: ''
        });
        setShowLessonModal(false);
        setSelectedChapterId('');
        alert('Leçon ajoutée avec succès !');
      }
    } catch (error: any) {
      console.error('Erreur lors de l\'ajout de la leçon:', error);
      alert(error.response?.data?.message || 'Erreur lors de l\'ajout');
    }
  };

  const handleVideoUpload = async (lessonId: string, file: File) => {
    try {
      const response = await courseService.uploadLessonVideo(lessonId, file);
      if (response.success && response.data) {
        // Mettre à jour la leçon dans l l'état
        setChapters(prev => prev.map(chapter => ({
          ...chapter,
          lessons: chapter.lessons?.map(lesson => 
            lesson.id === lessonId 
              ? { ...lesson, videoUrl: response.data!.videoUrl }
              : lesson
          )
        })));
        alert('Vidéo uploadée avec succès !');
      }
    } catch (error: any) {
      console.error('Erreur lors de l\'upload vidéo:', error);
      alert(error.response?.data?.message || 'Erreur lors de l\'upload');
    }
  };

  const handlePublishCourse = async () => {
    if (!course?.id) return;

    if (!confirm('Êtes-vous sûr de vouloir publier ce cours ? Il sera visible par tous les utilisateurs.')) {
      return;
    }

    try {
      const response = await courseService.publishCourse(course.id);
      if (response.success && response.data) {
        setCourse(response.data);
        alert('Cours publié avec succès !');
      }
    } catch (error: any) {
      console.error('Erreur lors de la publication:', error);
      alert(error.response?.data?.message || 'Erreur lors de la publication');
    }
  };

  const handleDeleteCourse = async () => {
    if (!course?.id) return;

    if (!confirm('Êtes-vous sûr de vouloir supprimer ce cours ? Cette action est irréversible.')) {
      return;
    }

    try {
      await courseService.deleteCourse(course.id);
      alert('Cours supprimé avec succès !');
      navigate('/admin');
    } catch (error: any) {
      console.error('Erreur lors de la suppression:', error);
      alert(error.response?.data?.message || 'Erreur lors de la suppression');
    }
  };

  const totalDurationMinutes = (parseInt(duration.hours, 10) || 0) * 60 + (parseInt(duration.minutes, 10) || 0);
  const isFormInvalid = !basicForm.title.trim() || !basicForm.description.trim() || !basicForm.categoryId || totalDurationMinutes === 0;

  // Helper function to format duration for display
  const formatDuration = (hours: string, minutes: string) => {
    const h = parseInt(hours, 10) || 0;
    const m = parseInt(minutes, 10) || 0;

    if (h === 0 && m === 0) {
      return '';
    }

    let result = '';
    if (h > 0) {
      result += `${h}h`;
    }
    if (m > 0) {
      result += `${m}mn`;
    }
    return result;
  };

  // Helper function to parse duration string (e.g., "1h25mn") into hours and minutes
  const parseDurationString = (durationString: string) => {
    const hoursMatch = durationString.match(/(\d+)h/);
    const minutesMatch = durationString.match(/(\d+)mn/);

    const hours = hoursMatch ? parseInt(hoursMatch[1], 10) : 0;
    const minutes = minutesMatch ? parseInt(minutesMatch[1], 10) : 0;

    return { hours: hours.toString(), minutes: minutes.toString() };
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-neutral flex items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
          <p className="text-gray-600">Chargement du cours...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigate('/admin')}
              className="flex items-center space-x-2 text-gray-600 hover:text-textPrimary transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
              <span>Retour au dashboard</span>
            </button>
            <div className="h-6 w-px bg-gray-300"></div>
            <h1 className="text-3xl font-bold text-textPrimary">
              {id ? 'Modifier le cours' : 'Nouveau cours'}
            </h1>
          </div>

          <div className="flex items-center space-x-3">
            {course && (
              <>
                <span className={`px-3 py-1 rounded-full text-sm font-medium ${ 
                  course.status === 'PUBLISHED' 
                    ? 'bg-success/20 text-success' 
                    : course.status === 'DRAFT'
                    ? 'bg-yellow-100 text-yellow-800'
                    : 'bg-gray-100 text-gray-800'
                }`}> 
                  {courseService.getStatusLabel(course.status)}
                </span>
                
                {course.status === 'DRAFT' && (
                  <button
                    onClick={handlePublishCourse}
                    className="flex items-center space-x-2 bg-success text-white px-4 py-2 rounded-lg hover:bg-success/90 transition-colors"
                  >
                    <Send className="w-4 h-4" />
                    <span>Publier</span>
                  </button>
                )}
                
                <button
                  onClick={() => navigate(`/course/${course.id}`)}
                  className="flex items-center space-x-2 bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors"
                >
                  <Eye className="w-4 h-4" />
                  <span>Prévisualiser</span>
                </button>
              </>
            )}
          </div>
        </div>

        {/* Navigation des onglets */}
        <div className="flex space-x-2 mb-8 bg-white rounded-xl p-2 shadow-sm border border-gray-200 w-fit">
          {[ { id: 'basic', label: 'Informations de base', icon: BookOpen },
            { id: 'content', label: 'Contenu', icon: FileText, disabled: !course.id },
            { id: 'settings', label: 'Paramètres', icon: Settings, disabled: !course.id }
          ].map((tab) => {
            const IconComponent = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => !tab.disabled && setActiveTab(tab.id as any)}
                disabled={tab.disabled}
                className={`flex items-center space-x-2 px-6 py-3 rounded-lg font-medium transition-all duration-200 ${ 
                  activeTab === tab.id
                    ? 'bg-primary text-white shadow-md'
                    : tab.disabled
                    ? 'text-gray-400 cursor-not-allowed'
                    : 'text-gray-600 hover:text-textPrimary hover:bg-gray-50'
                }`}
              >
                <IconComponent className="w-4 h-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* Contenu des onglets */}
        <div className="space-y-8">
          {/* Onglet Informations de base */}
          {activeTab === 'basic' && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {errorMessage && (
                <div className="lg:col-span-3 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
                
                  <span className="block sm:inline">{errorMessage}</span>
                  <span className="absolute top-0 bottom-0 right-0 px-4 py-3" onClick={() => setErrorMessage(null)}>
                    <svg className="fill-current h-6 w-6 text-red-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><title>Close</title><path d="M14.348 14.849a1.2 1.2 0 0 1-1.697 0L10 11.819l-2.651 3.029a1.2 1.2 0 1 1-1.697-1.697l2.758-3.15-2.759-3.152a1.2 1.2 0 1 1 1.697-1.697L10 8.183l2.651-3.031a1.2 1.2 0 1 1 1.697 1.697l-2.758 3.152 2.758 3.15a1.2 1.2 0 0 1 0 1.698z"/></svg>
                  </span>
                </div>
              )}
              {/* Formulaire principal */}
              <div className="lg:col-span-2 space-y-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                  <h3 className="text-lg font-semibold text-textPrimary mb-6">Informations générales</h3>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Titre du cours *
                      </label>
                      <input
                        type="text"
                        value={basicForm.title}
                        onChange={(e) => setBasicForm(prev => ({ ...prev, title: e.target.value }))}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary"
                        placeholder="Ex: Introduction à React"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Description *
                      </label>
                      <textarea
                        value={basicForm.description}
                        onChange={(e) => setBasicForm(prev => ({ ...prev, description: e.target.value }))}
                        rows={5}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary"
                        placeholder="Décrivez votre cours en quelques mots..."
                      ></textarea>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Catégorie *
                        </label>
                        <select
                          value={basicForm.categoryId}
                          onChange={(e) => setBasicForm(prev => ({ ...prev, categoryId: e.target.value }))}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary bg-white"
                        >
                          <option value="" disabled>Sélectionner une catégorie</option>
                          {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Niveau *
                        </label>
                        <select
                          value={basicForm.level}
                          onChange={(e) => setBasicForm(prev => ({ ...prev, level: e.target.value as CourseLevel }))}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary bg-white"
                        >
                          <option value="DEBUTANT">Débutant</option>
                          <option value="INTERMEDIAIRE">Intermédiaire</option>
                          <option value="AVANCE">Avancé</option>
                        </select>
                      </div>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Durée *
                      </label>
                      <div className="flex items-center space-x-2">
                        <input
                          type="number"
                          min="0"
                          value={duration.hours}
                          onChange={(e) => {
                            const value = e.target.value;
                            if (value === '' || /^[0-9]+$/.test(value)) {
                                setDuration(prev => ({ ...prev, hours: value }))
                            }
                          }}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary"
                          placeholder="Heures"
                        />
                        <span className="text-gray-500">:
                        </span>
                        <input
                          type="number"
                          min="0"
                          max="59"
                          value={duration.minutes}
                          onChange={(e) => {
                            const value = e.target.value;
                            if (value === '' || (/^[0-9]+$/.test(value) && parseInt(value, 10) < 60)) {
                                setDuration(prev => ({ ...prev, minutes: value }))
                            }
                          }}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary"
                          placeholder="Minutes"
                        />
                        {formatDuration(duration.hours, duration.minutes) && (
                          <span className="text-gray-500 text-sm">
                            ({formatDuration(duration.hours, duration.minutes)})
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="flex justify-end mt-6">
                    <button
                      onClick={handleSaveBasicInfo}
                      disabled={saving || isFormInvalid}
                      className="flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                    >
                      <Save className="w-4 h-4" />
                      <span>{saving ? 'Sauvegarde...' : (id ? 'Enregistrer les modifications' : 'Créer le cours')}</span>
                    </button>
                  </div>
                </div>
              </div>

              {/* Sidebar - Image de couverture */}
              <div className="space-y-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                  <h3 className="text-lg font-semibold text-textPrimary mb-4">Image de couverture</h3>
                  
                  <div className="space-y-4">
                    {imagePreview && (
                      <div className="aspect-video rounded-lg overflow-hidden bg-gray-100">
                        <img
                          src={imagePreview}
                          alt="Aperçu"
                          className="w-full h-full object-cover"
                        />
                      </div>
                    )}

                    <div>
                      <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        className="hidden"
                        id="course-image"
                      />
                      <label
                        htmlFor="course-image"
                        className="flex items-center justify-center space-x-2 w-full py-3 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary transition-colors cursor-pointer"
                      >
                        <Upload className="w-5 h-5 text-gray-500" />
                        <span className="text-gray-600">Choisir une image</span>
                      </label>
                    </div>

                    {
                      selectedImage && course?.id && (
                        <button
                          onClick={() => handleImageUpload(course.id!)}
                          disabled={imageUploading}
                          className="w-full bg-primary text-white py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:bg-gray-300"
                        >
                          {imageUploading ? 'Upload en cours...' : 'Uploader l\'image'}
                        </button>
                      )
                    }

                    <p className="text-xs text-gray-500">
                      Formats acceptés: JPG, PNG, GIF, WebP (max 5MB)
                    </p>
                  </div>
                </div>

                {course && (
                  <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <h3 className="text-lg font-semibold text-textPrimary mb-4">Statistiques</h3>
                    <div className="space-y-3">
                      <div className="flex justify-between">
                        <span className="text-gray-600">Étudiants:</span>
                        <span className="font-semibold">{course.studentsCount ?? 0}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">Note:</span>
                        <span className="font-semibold">{course.rating?.toFixed(1)}/5</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">Chapitres:</span>
                        <span className="font-semibold">{chapters.length}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">Leçons:</span>
                        <span className="font-semibold">
                          {chapters.reduce((total, chapter) => total + (chapter.lessons?.length || 0), 0)}
                        </span>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Onglet Contenu */}
          {activeTab === 'content' && course && (
            <div className="space-y-6">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-lg font-semibold text-textPrimary">Structure du cours</h3>
                  <button
                    onClick={() => setShowChapterModal(true)}
                    className="flex items-center space-x-2 bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors"
                  >
                    <Plus className="w-4 h-4" />
                    <span>Ajouter un chapitre</span>
                  </button>
                </div>

                {/* Liste des chapitres */}
                <div className="space-y-4">
                  {chapters.length === 0 ? (
                    <div className="text-center py-12">
                      <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                      <h4 className="text-lg font-medium text-gray-900 mb-2">Aucun chapitre</h4>
                      <p className="text-gray-600 mb-6">Commencez par ajouter un chapitre à votre cours.</p>
                      <button
                        onClick={() => setShowChapterModal(true)}
                        className="bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-colors"
                      >
                        Créer le premier chapitre
                      </button>
                    </div>
                  ) : (
                    chapters.map((chapter, chapterIndex) => (
                      <div key={chapter.id} className="border border-gray-200 rounded-lg overflow-hidden">
                        {/* En-tête du chapitre */}
                        <div className="bg-gray-50 p-4 flex items-center justify-between">
                          <div className="flex items-center space-x-3">
                            <div className="flex items-center space-x-2 cursor-grab">
                              <GripVertical className="w-4 h-4 text-gray-400" />
                              <span className="text-sm font-medium text-gray-500">
                                Chapitre {chapterIndex + 1}
                              </span>
                            </div>
                            <h4 className="font-semibold text-textPrimary">{chapter.title}</h4>
                          </div>
                          <div className="flex items-center space-x-2">
                            <span className="text-sm text-gray-500">
                              {chapter.lessons?.length || 0} leçon(s)
                            </span>
                            <button
                              onClick={() => {
                                setSelectedChapterId(chapter.id);
                                setShowLessonModal(true);
                              }}
                              className="flex items-center space-x-1 bg-primary text-white px-3 py-1 rounded text-sm hover:bg-primary/90 transition-colors"
                            >
                              <Plus className="w-3 h-3" />
                              <span>Leçon</span>
                            </button>
                            <button
                              onClick={() => setExpandedChapter(
                                expandedChapter === chapter.id ? null : chapter.id
                              )}
                              className="text-gray-600 hover:text-textPrimary transition-colors"
                            >
                              {expandedChapter === chapter.id ? '−' : '+'}
                            </button>
                          </div>
                        </div>

                        {/* Leçons du chapitre */}
                        {expandedChapter === chapter.id && (
                          <div className="p-4 border-t border-gray-200">
                            {chapter.lessons && chapter.lessons.length > 0 ? (
                              <div className="space-y-3">
                                {chapter.lessons.map((lesson, lessonIndex) => (
                                  <div key={lesson.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                    <div className="flex items-center space-x-3">
                                      <span className="text-sm text-gray-500">
                                        {lessonIndex + 1}.
                                      </span>
                                      <div className="flex items-center space-x-2">
                                        {lesson.contentType === 'VIDEO' && (
                                          <Video className="w-4 h-4 text-red-500" />
                                        )}
                                        {lesson.contentType === 'TEXT' && (
                                          <FileText className="w-4 h-4 text-blue-500" />
                                        )}
                                        <span className="font-medium">{lesson.title}</span>
                                      </div>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                      <span className="text-xs bg-gray-200 px-2 py-1 rounded">
                                        {courseService.getContentTypeLabel(lesson.contentType)}
                                      </span>
                                      {lesson.contentType === 'VIDEO' && !lesson.videoUrl && (
                                        <div>
                                          <input
                                            type="file"
                                            accept="video/*"
                                            onChange={(e) => {
                                              const file = e.target.files?.[0];
                                              if (file) {
                                                handleVideoUpload(lesson.id, file);
                                              }
                                            }}
                                            className="hidden"
                                            id={`video-${lesson.id}`}
                                          />
                                          <label
                                            htmlFor={`video-${lesson.id}`}
                                            className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded cursor-pointer hover:bg-yellow-200 transition-colors"
                                          >
                                            Ajouter vidéo
                                          </label>
                                        </div>
                                      )}
                                      <button className="text-gray-400 hover:text-textPrimary transition-colors">
                                        <Edit3 className="w-4 h-4" />
                                      </button>
                                    </div>
                                  </div>
                                ))}
                              </div>
                            ) : (
                              <div className="text-center py-6">
                                <p className="text-gray-500 mb-3">Aucune leçon dans ce chapitre</p>
                                <button
                                  onClick={() => {
                                    setSelectedChapterId(chapter.id);
                                    setShowLessonModal(true);
                                  }}
                                  className="text-primary hover:text-primary/80 text-sm font-medium"
                                >
                                  Ajouter la première leçon
                                </button>
                              </div>
                            )}
                          </div>
                        )}
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Onglet Paramètres */}
          {activeTab === 'settings' && course && (
            <div className="space-y-6">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h3 className="text-lg font-semibold text-textPrimary mb-6">Paramètres du cours</h3>
                
                <div className="space-y-6">
                  <div>
                    <h4 className="font-medium text-textPrimary mb-3">Statut de publication</h4>
                    <div className="flex items-center space-x-4">
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${ 
                        course.status === 'PUBLISHED' 
                          ? 'bg-success/20 text-success' 
                          : 'bg-yellow-100 text-yellow-800'
                      }`}> 
                        {courseService.getStatusLabel(course.status)}
                      </span>
                      {course.status === 'DRAFT' && (
                        <button
                          onClick={handlePublishCourse}
                          className="bg-success text-white px-4 py-2 rounded-lg hover:bg-success/90 transition-colors"
                        >
                          Publier le cours
                        </button>
                      )}
                    </div>
                  </div>

                  <div className="border-t pt-6">
                    <h4 className="font-medium text-red-600 mb-3">Zone de danger</h4>
                    <p className="text-gray-600 mb-4">
                      La suppression du cours est définitive et supprimera tous les contenus associés.
                    </p>
                    <button
                      onClick={handleDeleteCourse}
                      className="flex items-center space-x-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                      <span>Supprimer le cours</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Modal Ajout de chapitre */}
      {showChapterModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-md mx-4">
            <h3 className="text-lg font-semibold text-textPrimary mb-4">Nouveau chapitre</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Titre du chapitre *
                </label>
                <input
                  type="text"
                  value={chapterForm.title}
                  onChange={(e) => setChapterForm({ title: e.target.value })}
                  placeholder="Ex: Introduction au développement web"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                  autoFocus
                />
              </div>
            </div>

            <div className="flex justify-end space-x-3 mt-6">
              <button
                onClick={() => {
                  setShowChapterModal(false);
                  setChapterForm({ title: '' });
                }}
                className="px-4 py-2 text-gray-600 hover:text-textPrimary transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={handleAddChapter}
                disabled={!chapterForm.title.trim()}
                className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:bg-gray-300"
              >
                Ajouter
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Ajout de leçon */}
      {showLessonModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-lg mx-4">
            <h3 className="text-lg font-semibold text-textPrimary mb-4">Nouvelle leçon</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Titre de la leçon *
                </label>
                <input
                  type="text"
                  value={lessonForm.title}
                  onChange={(e) => setLessonForm(prev => ({ ...prev, title: e.target.value }))}
                  placeholder="Ex: Les bases du HTML"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                  autoFocus
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Type de contenu *
                </label>
                <select
                  value={lessonForm.contentType}
                  onChange={(e) => setLessonForm(prev => ({ ...prev, contentType: e.target.value as ContentType }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                >
                  <option value="TEXT">Texte</option>
                  <option value="VIDEO">Vidéo</option>
                  <option value="DOCUMENT">Document</option>
                </select>
              </div>

              {lessonForm.contentType === 'TEXT' && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Contenu
                  </label>
                  <textarea
                    value={lessonForm.content}
                    onChange={(e) => setLessonForm(prev => ({ ...prev, content: e.target.value }))}
                    placeholder="Contenu de la leçon..."
                    rows={4}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                  />
                </div>
              )}

              {lessonForm.contentType === 'VIDEO' && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    URL de la vidéo (optionnel)
                  </label>
                  <input
                    type="url"
                    value={lessonForm.videoUrl}
                    onChange={(e) => setLessonForm(prev => ({ ...prev, videoUrl: e.target.value }))}
                    placeholder="https://youtube.com/watch?v=..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    Vous pourrez uploader une vidéo plus tard
                  </p>
                </div>
              )}
            </div>

            <div className="flex justify-end space-x-3 mt-6">
              <button
                onClick={() => {
                  setShowLessonModal(false);
                  setLessonForm({
                    title: '',
                    content: '',
                    contentType: 'TEXT',
                    videoUrl: ''
                  });
                  setSelectedChapterId('');
                }}
                className="px-4 py-2 text-gray-600 hover:text-textPrimary transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={handleAddLesson}
                disabled={!lessonForm.title.trim()}
                className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:bg-gray-300"
              >
                Ajouter
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
