import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  Clock,
  BookOpen,
  BarChart,
  Star,
  Play,
  CheckCircle,
  ChevronDown,
  ArrowLeft,
  Users,
} from "lucide-react";
import { mockCourses, mockChapters } from "../data/mockData";

export default function CourseDetail() {
  const { id } = useParams();
  const [expandedChapter, setExpandedChapter] = useState<string | null>("1");

  const course = mockCourses.find((c) => c.id === id);

  if (!course) {
    return (
      <div className="min-h-screen bg-neutral flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-textPrimary mb-4">
            Cours non trouvé
          </h1>
          <Link
            to="/courses"
            className="text-primary hover:text-primary/80 transition-colors"
          >
            Retour aux cours
          </Link>
        </div>
      </div>
    );
  }

  const getLevelColor = (level: string) => {
    switch (level) {
      case "Débutant":
        return "bg-green-100 text-green-800";
      case "Intermédiaire":
        return "bg-yellow-100 text-yellow-800";
      case "Avancé":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const toggleChapter = (chapterId: string) => {
    setExpandedChapter(expandedChapter === chapterId ? null : chapterId);
  };

  const getTotalLessons = () => {
    return mockChapters.reduce(
      (total, chapter) => total + chapter.lessons.length,
      0
    );
  };

  const getCompletedLessons = () => {
    return mockChapters.reduce(
      (total, chapter) =>
        total + chapter.lessons.filter((lesson) => lesson.isCompleted).length,
      0
    );
  };

  return (
    <div className="min-h-screen bg-neutral">
      {/* Header */}
      <div className="relative">
        {/* Hero Image */}
        <div className="h-96 bg-gradient-to-r from-black/60 to-black/40 relative overflow-hidden">
          <img
            src={course.image}
            alt={course.title}
            className="w-full h-full object-cover absolute inset-0"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent" />

          {/* Content */}
          <div className="relative h-full flex items-end">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-12 w-full">
              <div className="max-w-4xl">
                {/* Breadcrumb */}
                <nav className="mb-4">
                  <Link
                    to="/courses"
                    className="inline-flex items-center text-white/80 hover:text-white transition-colors"
                  >
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    Retour aux cours
                  </Link>
                </nav>

                {/* Course Info */}
                <div className="mb-4">
                  <span className="inline-block px-3 py-1 bg-accent/90 text-white rounded-full text-sm font-medium mb-4">
                    {course.category}
                  </span>
                </div>

                <h1 className="text-4xl lg:text-5xl font-bold text-white mb-4 leading-tight">
                  {course.title}
                </h1>

                <p className="text-xl text-white/90 mb-6 leading-relaxed max-w-3xl">
                  {course.description}
                </p>

                {/* Meta Info */}
                <div className="flex flex-wrap items-center gap-6 text-white/90">
                  <div className="flex items-center space-x-2">
                    <Clock className="w-5 h-5" />
                    <span>{course.duration}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <BookOpen className="w-5 h-5" />
                    <span>{getTotalLessons()} leçons</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <BarChart className="w-5 h-5" />
                    <span
                      className={`px-3 py-1 rounded-full text-sm font-medium ${getLevelColor(
                        course.level
                      )} text-gray-800`}
                    >
                      {course.level}
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Users className="w-5 h-5" />
                    <span>1,234 étudiants</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Star className="w-5 h-5 text-yellow-400" />
                    <span>4.8 (156 avis)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid lg:grid-cols-3 gap-12">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Progress Bar (if enrolled) */}
            {course.isEnrolled && course.progress !== undefined && (
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-8">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-semibold text-textPrimary">
                    Votre progression
                  </h2>
                  <span className="text-primary font-medium">
                    {course.progress}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 mb-2">
                  <div
                    className="bg-gradient-to-r from-primary to-success h-3 rounded-full transition-all duration-500"
                    style={{ width: `${course.progress}%` }}
                  ></div>
                </div>
                <p className="text-sm text-gray-600">
                  {getCompletedLessons()} sur {getTotalLessons()} leçons
                  terminées
                </p>
              </div>
            )}

            {/* Course Description */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-8">
              <h2 className="text-2xl font-bold text-textPrimary mb-6">
                À propos de ce cours
              </h2>
              <div className="prose prose-lg max-w-none text-gray-700">
                <p className="mb-4">
                  Ce cours complet vous permettra de maîtriser les fondamentaux
                  du développement web moderne. Vous apprendrez à créer des
                  sites web interactifs et responsives en utilisant les
                  technologies les plus demandées sur le marché.
                </p>
                <p className="mb-4">
                  À travers des projets pratiques et des exercices progressifs,
                  vous développerez une compréhension solide des concepts clés
                  et des meilleures pratiques du développement web.
                </p>

                <h3 className="text-xl font-semibold text-textPrimary mt-8 mb-4">
                  Ce que vous allez apprendre
                </h3>
                <ul className="space-y-2">
                  <li className="flex items-start space-x-3">
                    <CheckCircle className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                    <span>Les bases du HTML5 et de la sémantique web</span>
                  </li>
                  <li className="flex items-start space-x-3">
                    <CheckCircle className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                    <span>
                      Le CSS3 avancé et les techniques de layout modernes
                    </span>
                  </li>
                  <li className="flex items-start space-x-3">
                    <CheckCircle className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                    <span>
                      JavaScript ES6+ et la programmation orientée objet
                    </span>
                  </li>
                  <li className="flex items-start space-x-3">
                    <CheckCircle className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                    <span>Le développement responsive et mobile-first</span>
                  </li>
                  <li className="flex items-start space-x-3">
                    <CheckCircle className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                    <span>
                      Les outils de développement et les bonnes pratiques
                    </span>
                  </li>
                </ul>

                <h3 className="text-xl font-semibold text-textPrimary mt-8 mb-4">
                  Prérequis
                </h3>
                <ul className="space-y-2">
                  <li>
                    • Aucune expérience préalable en programmation requise
                  </li>
                  <li>• Un ordinateur avec accès à Internet</li>
                  <li>• Motivation pour apprendre et pratiquer</li>
                </ul>
              </div>
            </div>

            {/* Course Curriculum */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
              <h2 className="text-2xl font-bold text-textPrimary mb-6">
                Contenu du cours
              </h2>

              <div className="space-y-4">
                {mockChapters.map((chapter) => (
                  <div
                    key={chapter.id}
                    className="border border-gray-200 rounded-lg"
                  >
                    <button
                      onClick={() => toggleChapter(chapter.id)}
                      className="w-full flex items-center justify-between p-4 text-left hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex items-center space-x-3">
                        <div className="w-8 h-8 bg-primary/10 rounded-lg flex items-center justify-center">
                          <span className="text-primary font-medium text-sm">
                            {chapter.id}
                          </span>
                        </div>
                        <div>
                          <h3 className="font-semibold text-textPrimary">
                            {chapter.title}
                          </h3>
                          <p className="text-sm text-gray-600">
                            {chapter.lessons.length} leçons
                          </p>
                        </div>
                      </div>
                      <ChevronDown
                        className={`w-5 h-5 text-gray-400 transition-transform ${
                          expandedChapter === chapter.id ? "rotate-180" : ""
                        }`}
                      />
                    </button>

                    {expandedChapter === chapter.id && (
                      <div className="border-t border-gray-200">
                        {chapter.lessons.map((lesson) => (
                          <div
                            key={lesson.id}
                            className="flex items-center justify-between p-4 pl-16 hover:bg-gray-50 transition-colors"
                          >
                            <div className="flex items-center space-x-3">
                              {lesson.isCompleted ? (
                                <CheckCircle className="w-5 h-5 text-success" />
                              ) : (
                                <Play className="w-5 h-5 text-gray-400" />
                              )}
                              <div>
                                <h4 className="font-medium text-textPrimary">
                                  {lesson.title}
                                </h4>
                                <p className="text-sm text-gray-600">
                                  {lesson.duration}
                                </p>
                              </div>
                            </div>
                            {course.isEnrolled && (
                              <Link
                                to={`/course/${course.id}/lesson/${lesson.id}`}
                                className="text-primary hover:text-primary/80 transition-colors text-sm font-medium"
                              >
                                {lesson.isCompleted ? "Revoir" : "Commencer"}
                              </Link>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="sticky top-24">
              {/* Enrollment Card */}
              <div className="bg-white rounded-xl shadow-lg border border-gray-200 p-8 mb-6">
                {course.isEnrolled ? (
                  <>
                    <h3 className="text-xl font-bold text-textPrimary mb-6">
                      Votre parcours
                    </h3>

                    {/* Progress */}
                    <div className="mb-6">
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-sm font-medium text-gray-700">
                          Progression
                        </span>
                        <span className="text-sm font-medium text-primary">
                          {course.progress}%
                        </span>
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-gradient-to-r from-primary to-success h-2 rounded-full transition-all duration-300"
                          style={{ width: `${course.progress}%` }}
                        ></div>
                      </div>
                    </div>

                    <Link
                      to={`/player/${course.id}`}
                      className="w-full bg-primary text-white py-4 rounded-lg font-semibold hover:bg-primary/90 transition-colors flex items-center justify-center space-x-2 mb-4"
                    >
                      <Play className="w-5 h-5" />
                      <span>
                        {course.progress && course.progress > 0
                          ? "Continuer le cours"
                          : "Commencer le cours"}
                      </span>
                    </Link>

                    <button className="w-full border border-gray-300 text-textPrimary py-3 rounded-lg font-medium hover:bg-gray-50 transition-colors">
                      Télécharger les ressources
                    </button>
                  </>
                ) : (
                  <>
                    <div className="text-center mb-6">
                      <div className="text-3xl font-bold text-textPrimary mb-2">
                        Gratuit
                      </div>
                      <p className="text-gray-600">Accès illimité à vie</p>
                    </div>

                    <Link to="/auth">
                      <button className="w-full bg-primary text-white py-4 rounded-lg font-semibold hover:bg-primary/90 transition-colors mb-4">
                        S'inscrire gratuitement
                      </button>
                    </Link>

                    <p className="text-center text-sm text-gray-500 mb-6">
                      30 jours de garantie satisfait ou remboursé
                    </p>

                    <div className="space-y-3 text-sm">
                      <div className="flex items-center space-x-3">
                        <CheckCircle className="w-5 h-5 text-success flex-shrink-0" />
                        <span>Accès illimité au cours</span>
                      </div>
                      <div className="flex items-center space-x-3">
                        <CheckCircle className="w-5 h-5 text-success flex-shrink-0" />
                        <span>Certificat de completion</span>
                      </div>
                      <div className="flex items-center space-x-3">
                        <CheckCircle className="w-5 h-5 text-success flex-shrink-0" />
                        <span>Support communauté</span>
                      </div>
                      <div className="flex items-center space-x-3">
                        <CheckCircle className="w-5 h-5 text-success flex-shrink-0" />
                        <span>Ressources téléchargeables</span>
                      </div>
                    </div>
                  </>
                )}
              </div>

              {/* Course Stats */}
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h3 className="font-semibold text-textPrimary mb-4">
                  Informations du cours
                </h3>

                <div className="space-y-3 text-sm">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Durée totale</span>
                    <span className="font-medium">{course.duration}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Nombre de leçons</span>
                    <span className="font-medium">{getTotalLessons()}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Niveau</span>
                    <span
                      className={`px-2 py-1 rounded text-xs font-medium ${getLevelColor(
                        course.level
                      )}`}
                    >
                      {course.level}
                    </span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Langue</span>
                    <span className="font-medium">Français</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Dernière mise à jour</span>
                    <span className="font-medium">Janvier 2025</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
