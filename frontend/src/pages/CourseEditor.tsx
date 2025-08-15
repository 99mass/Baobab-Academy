import React, { useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, Eye, Upload, Plus, Trash2, GripVertical } from 'lucide-react';

export default function CourseEditor() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = id !== 'new';
  
  const [courseData, setCourseData] = useState({
    title: '',
    description: '',
    category: '',
    level: 'Débutant' as 'Débutant' | 'Intermédiaire' | 'Avancé',
    duration: '',
    image: '',
    chapters: [
      {
        id: '1',
        title: '',
        lessons: [
          {
            id: '1-1',
            title: '',
            content: '',
            duration: ''
          }
        ]
      }
    ]
  });

  const categories = ['Développement Web', 'Design', 'Marketing', 'Management', 'Technologie', 'Business'];
  const levels: Array<'Débutant' | 'Intermédiaire' | 'Avancé'> = ['Débutant', 'Intermédiaire', 'Avancé'];

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setCourseData({
      ...courseData,
      [e.target.name]: e.target.value
    });
  };

  const addChapter = () => {
    const newChapterId = (courseData.chapters.length + 1).toString();
    setCourseData({
      ...courseData,
      chapters: [
        ...courseData.chapters,
        {
          id: newChapterId,
          title: '',
          lessons: [
            {
              id: `${newChapterId}-1`,
              title: '',
              content: '',
              duration: ''
            }
          ]
        }
      ]
    });
  };

  const removeChapter = (chapterId: string) => {
    setCourseData({
      ...courseData,
      chapters: courseData.chapters.filter(chapter => chapter.id !== chapterId)
    });
  };

  const updateChapter = (chapterId: string, field: string, value: string) => {
    setCourseData({
      ...courseData,
      chapters: courseData.chapters.map(chapter =>
        chapter.id === chapterId
          ? { ...chapter, [field]: value }
          : chapter
      )
    });
  };

  const addLesson = (chapterId: string) => {
    setCourseData({
      ...courseData,
      chapters: courseData.chapters.map(chapter =>
        chapter.id === chapterId
          ? {
              ...chapter,
              lessons: [
                ...chapter.lessons,
                {
                  id: `${chapterId}-${chapter.lessons.length + 1}`,
                  title: '',
                  content: '',
                  duration: ''
                }
              ]
            }
          : chapter
      )
    });
  };

  const removeLesson = (chapterId: string, lessonId: string) => {
    setCourseData({
      ...courseData,
      chapters: courseData.chapters.map(chapter =>
        chapter.id === chapterId
          ? {
              ...chapter,
              lessons: chapter.lessons.filter(lesson => lesson.id !== lessonId)
            }
          : chapter
      )
    });
  };

  const updateLesson = (chapterId: string, lessonId: string, field: string, value: string) => {
    setCourseData({
      ...courseData,
      chapters: courseData.chapters.map(chapter =>
        chapter.id === chapterId
          ? {
              ...chapter,
              lessons: chapter.lessons.map(lesson =>
                lesson.id === lessonId
                  ? { ...lesson, [field]: value }
                  : lesson
              )
            }
          : chapter
      )
    });
  };

  const handleSave = () => {
    console.log('Saving course:', courseData);
    // Here you would typically save to your backend
    navigate('/admin');
  };

  const handlePreview = () => {
    console.log('Previewing course:', courseData);
    // Here you would typically show a preview
  };

  return (
    <div className="min-h-screen bg-neutral">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <nav className="mb-4">
            <Link 
              to="/admin" 
              className="inline-flex items-center text-gray-600 hover:text-primary transition-colors"
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Retour au tableau de bord
            </Link>
          </nav>
          
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-textPrimary">
                {isEditing ? 'Modifier le cours' : 'Créer un nouveau cours'}
              </h1>
              <p className="text-gray-600 mt-2">
                {isEditing ? 'Modifiez les informations de votre cours' : 'Créez un nouveau cours pour votre plateforme'}
              </p>
            </div>
            
            <div className="flex items-center space-x-4">
              <button
                onClick={handlePreview}
                className="flex items-center space-x-2 px-6 py-3 border border-gray-300 text-textPrimary rounded-lg hover:bg-gray-50 transition-colors font-medium"
              >
                <Eye className="w-5 h-5" />
                <span>Prévisualiser</span>
              </button>
              <button
                onClick={handleSave}
                className="flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-colors font-medium"
              >
                <Save className="w-5 h-5" />
                <span>Sauvegarder</span>
              </button>
            </div>
          </div>
        </div>

        {/* Form */}
        <div className="space-y-8">
          {/* Basic Information */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <h2 className="text-xl font-semibold text-textPrimary mb-6">Informations générales</h2>
            
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="lg:col-span-2">
                <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                  Titre du cours *
                </label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={courseData.title}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                  placeholder="Ex: Introduction au Développement Web"
                  required
                />
              </div>

              <div className="lg:col-span-2">
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                  Description *
                </label>
                <textarea
                  id="description"
                  name="description"
                  value={courseData.description}
                  onChange={handleInputChange}
                  rows={4}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors resize-none"
                  placeholder="Décrivez ce que les étudiants apprendront dans ce cours..."
                  required
                />
              </div>

              <div>
                <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-2">
                  Catégorie *
                </label>
                <select
                  id="category"
                  name="category"
                  value={courseData.category}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                  required
                >
                  <option value="">Choisir une catégorie</option>
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="level" className="block text-sm font-medium text-gray-700 mb-2">
                  Niveau *
                </label>
                <select
                  id="level"
                  name="level"
                  value={courseData.level}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                  required
                >
                  {levels.map(level => (
                    <option key={level} value={level}>{level}</option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="duration" className="block text-sm font-medium text-gray-700 mb-2">
                  Durée estimée *
                </label>
                <input
                  type="text"
                  id="duration"
                  name="duration"
                  value={courseData.duration}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                  placeholder="Ex: 12 heures"
                  required
                />
              </div>

              <div>
                <label htmlFor="image" className="block text-sm font-medium text-gray-700 mb-2">
                  Image de couverture
                </label>
                <div className="flex items-center space-x-4">
                  <input
                    type="text"
                    id="image"
                    name="image"
                    value={courseData.image}
                    onChange={handleInputChange}
                    className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                    placeholder="URL de l'image"
                  />
                  <button
                    type="button"
                    className="flex items-center space-x-2 px-4 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <Upload className="w-4 h-4" />
                    <span>Upload</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Course Content */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-semibold text-textPrimary">Contenu du cours</h2>
              <button
                onClick={addChapter}
                className="flex items-center space-x-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors"
              >
                <Plus className="w-4 h-4" />
                <span>Ajouter un chapitre</span>
              </button>
            </div>

            <div className="space-y-6">
              {courseData.chapters.map((chapter, chapterIndex) => (
                <div key={chapter.id} className="border border-gray-200 rounded-lg p-6">
                  {/* Chapter Header */}
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <div className="w-8 h-8 bg-primary/10 rounded-lg flex items-center justify-center">
                        <GripVertical className="w-4 h-4 text-primary" />
                      </div>
                      <span className="font-medium text-gray-600">Chapitre {chapterIndex + 1}</span>
                    </div>
                    {courseData.chapters.length > 1 && (
                      <button
                        onClick={() => removeChapter(chapter.id)}
                        className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>

                  {/* Chapter Title */}
                  <div className="mb-4">
                    <input
                      type="text"
                      value={chapter.title}
                      onChange={(e) => updateChapter(chapter.id, 'title', e.target.value)}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                      placeholder="Titre du chapitre"
                    />
                  </div>

                  {/* Lessons */}
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <h4 className="font-medium text-gray-700">Leçons</h4>
                      <button
                        onClick={() => addLesson(chapter.id)}
                        className="flex items-center space-x-2 px-3 py-2 text-primary border border-primary rounded-lg hover:bg-primary/5 transition-colors text-sm"
                      >
                        <Plus className="w-4 h-4" />
                        <span>Ajouter une leçon</span>
                      </button>
                    </div>

                    {chapter.lessons.map((lesson, lessonIndex) => (
                      <div key={lesson.id} className="bg-gray-50 rounded-lg p-4">
                        <div className="flex items-center justify-between mb-3">
                          <span className="text-sm font-medium text-gray-600">
                            Leçon {lessonIndex + 1}
                          </span>
                          {chapter.lessons.length > 1 && (
                            <button
                              onClick={() => removeLesson(chapter.id, lesson.id)}
                              className="p-1 text-red-600 hover:bg-red-50 rounded transition-colors"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          )}
                        </div>

                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                          <div className="lg:col-span-2">
                            <input
                              type="text"
                              value={lesson.title}
                              onChange={(e) => updateLesson(chapter.id, lesson.id, 'title', e.target.value)}
                              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                              placeholder="Titre de la leçon"
                            />
                          </div>
                          <div>
                            <input
                              type="text"
                              value={lesson.duration}
                              onChange={(e) => updateLesson(chapter.id, lesson.id, 'duration', e.target.value)}
                              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                              placeholder="Ex: 15 min"
                            />
                          </div>
                        </div>

                        <div className="mt-3">
                          <textarea
                            value={lesson.content}
                            onChange={(e) => updateLesson(chapter.id, lesson.id, 'content', e.target.value)}
                            rows={3}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors resize-none"
                            placeholder="Contenu de la leçon..."
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end space-x-4 pt-8">
            <Link
              to="/admin"
              className="px-6 py-3 border border-gray-300 text-textPrimary rounded-lg hover:bg-gray-50 transition-colors font-medium"
            >
              Annuler
            </Link>
            <button
              onClick={handlePreview}
              className="flex items-center space-x-2 px-6 py-3 border border-gray-300 text-textPrimary rounded-lg hover:bg-gray-50 transition-colors font-medium"
            >
              <Eye className="w-5 h-5" />
              <span>Prévisualiser</span>
            </button>
            <button
              onClick={handleSave}
              className="flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-colors font-medium"
            >
              <Save className="w-5 h-5" />
              <span>{isEditing ? 'Sauvegarder les modifications' : 'Publier le cours'}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}