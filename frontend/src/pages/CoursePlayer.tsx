import { useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  ChevronLeft, 
  ChevronRight, 
  CheckCircle, 
  PlayCircle, 
  Menu, 
  X,
} from 'lucide-react';
import { mockChapters, mockCourses } from '../data/mockData';

export default function CoursePlayer() {
  const { id, lessonId } = useParams();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [currentLessonId, setCurrentLessonId] = useState(lessonId || '1-1');

  const course = mockCourses.find(c => c.id === id);
  
  // Find current lesson
  const currentLesson = mockChapters
    .flatMap(chapter => chapter.lessons)
    .find(lesson => lesson.id === currentLessonId);

  const currentChapter = mockChapters.find(chapter => 
    chapter.lessons.some(lesson => lesson.id === currentLessonId)
  );

  if (!course || !currentLesson || !currentChapter) {
    return (
      <div className="min-h-screen bg-neutral flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-textPrimary mb-4">Contenu non trouvé</h1>
          <Link to="/courses" className="text-primary hover:text-primary/80 transition-colors">
            Retour aux cours
          </Link>
        </div>
      </div>
    );
  }

  // Get all lessons flattened for navigation
  const allLessons = mockChapters.flatMap(chapter => chapter.lessons);
  const currentIndex = allLessons.findIndex(lesson => lesson.id === currentLessonId);
  const previousLesson = currentIndex > 0 ? allLessons[currentIndex - 1] : null;
  const nextLesson = currentIndex < allLessons.length - 1 ? allLessons[currentIndex + 1] : null;

  const navigateToLesson = (lessonId: string) => {
    setCurrentLessonId(lessonId);
    navigate(`/player/${id}/${lessonId}`, { replace: true });
  };

  const markAsCompleted = () => {
    // Here you would typically update the lesson completion status
    console.log('Marking lesson as completed:', currentLessonId);
  };

  return (
    <div className="min-h-screen bg-white flex">
      {/* Sidebar */}
      <div className={`${sidebarOpen ? 'w-80' : 'w-0'} pt-5 transition-all duration-300 overflow-hidden bg-white border-r border-gray-200 flex flex-col`}>
        {/* Sidebar Header */}
        <div className="p-4 border-b border-gray-200 bg-white">
          <div className="flex items-center justify-between mb-3">
            <Link 
              to={`/course/${id}`}
              className="flex items-center text-gray-600 hover:text-primary transition-colors text-sm"
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Retour au cours
            </Link>
            <button
              onClick={() => setSidebarOpen(false)}
              className="lg:hidden p-1 hover:bg-gray-100 rounded"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
          <h2 className="font-semibold text-textPrimary truncate">{course.title}</h2>
          
          {/* Progress */}
          <div className="mt-3">
            <div className="flex items-center justify-between text-sm text-gray-600 mb-1">
              <span>Progression</span>
              <span>{course.progress || 0}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div 
                className="bg-gradient-to-r from-primary to-success h-2 rounded-full transition-all duration-300" 
                style={{ width: `${course.progress || 0}%` }}
              ></div>
            </div>
          </div>
        </div>

        {/* Course Content */}
        <div className="flex-1 overflow-y-auto">
          {mockChapters.map((chapter) => (
            <div key={chapter.id} className="border-b border-gray-100 last:border-b-0">
              <div className="p-4 bg-gray-50">
                <h3 className="font-medium text-textPrimary">{chapter.title}</h3>
                <p className="text-sm text-gray-600">{chapter.lessons.length} leçons</p>
              </div>
              <div>
                {chapter.lessons.map((lesson) => (
                  <button
                    key={lesson.id}
                    onClick={() => navigateToLesson(lesson.id)}
                    className={`w-full p-4 text-left hover:bg-gray-50 transition-colors border-b border-gray-100 last:border-b-0 ${
                      currentLessonId === lesson.id ? 'bg-primary/5 border-r-2 border-r-primary' : ''
                    }`}
                  >
                    <div className="flex items-center space-x-3">
                      <div className="flex-shrink-0">
                        {lesson.isCompleted ? (
                          <CheckCircle className="w-5 h-5 text-success" />
                        ) : currentLessonId === lesson.id ? (
                          <PlayCircle className="w-5 h-5 text-primary" />
                        ) : (
                          <div className="w-5 h-5 border-2 border-gray-300 rounded-full" />
                        )}
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className={`font-medium truncate ${
                          currentLessonId === lesson.id ? 'text-primary' : 'text-textPrimary'
                        }`}>
                          {lesson.title}
                        </h4>
                        <p className="text-sm text-gray-600">{lesson.duration}</p>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col pt-5">
        {/* Top Bar */}
        <div className="bg-white border-b border-gray-200 p-4 flex items-center justify-between">
          <div className="flex items-center space-x-4">
            {!sidebarOpen && (
              <button
                onClick={() => setSidebarOpen(true)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <Menu className="w-5 h-5" />
              </button>
            )}
            <div>
              <h1 className="font-semibold text-textPrimary">{currentLesson.title}</h1>
              <p className="text-sm text-gray-600">{currentChapter.title}</p>
            </div>
          </div>

        </div>

        {/* Video/Content Area */}
        <div className="flex-1 bg-black flex items-center justify-center">
          <div className="max-w-4xl w-full mx-auto">
            {/* Mock Video Player */}
            <div className="aspect-video bg-gray-800 rounded-lg flex items-center justify-center">
              <div className="text-center text-white">
                <PlayCircle className="w-20 h-20 mx-auto mb-4 opacity-60" />
                <h3 className="text-xl font-semibold mb-2">{currentLesson.title}</h3>
                <p className="text-gray-300">Durée: {currentLesson.duration}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Lesson Content */}
        <div className="bg-white border-t border-gray-200">
          <div className="max-w-4xl mx-auto p-6">
            <div className="prose max-w-none mb-6">
              <h2 className="text-xl font-semibold text-textPrimary mb-4">{currentLesson.title}</h2>
              <div className="text-gray-700 leading-relaxed">
                <p className="mb-4">{currentLesson.content}</p>
                <p className="mb-4">
                  Dans cette leçon, nous explorerons les concepts fondamentaux qui vous permettront 
                  de comprendre et d'appliquer les techniques présentées. Prenez le temps de bien 
                  assimiler chaque notion avant de passer à la suite.
                </p>
                <p>
                  N'hésitez pas à revoir cette leçon plusieurs fois si nécessaire et à pratiquer 
                  les exercices proposés pour consolider vos acquis.
                </p>
              </div>
            </div>

            {/* Navigation */}
            <div className="flex items-center justify-between pt-6 border-t border-gray-200">
              <div>
                {previousLesson ? (
                  <button
                    onClick={() => navigateToLesson(previousLesson.id)}
                    className="flex items-center space-x-2 px-4 py-2 text-gray-600 hover:text-primary hover:bg-gray-50 rounded-lg transition-colors"
                  >
                    <ChevronLeft className="w-4 h-4" />
                    <span>Précédent</span>
                  </button>
                ) : (
                  <div />
                )}
              </div>

              <div className="flex items-center space-x-4">
                {!currentLesson.isCompleted && (
                  <button
                    onClick={markAsCompleted}
                    className="flex items-center space-x-2 px-6 py-3 bg-success text-white rounded-lg hover:bg-success/90 transition-colors font-medium"
                  >
                    <CheckCircle className="w-5 h-5" />
                    <span>Marquer comme terminé</span>
                  </button>
                )}
                
                {nextLesson && (
                  <button
                    onClick={() => navigateToLesson(nextLesson.id)}
                    className="flex items-center space-x-2 px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors font-medium"
                  >
                    <span>Suivant</span>
                    <ChevronRight className="w-4 h-4" />
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Mobile Sidebar Overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 lg:hidden z-40"
          onClick={() => setSidebarOpen(false)}
        />
      )}
    </div>
  );
}