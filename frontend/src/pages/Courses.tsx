import { useState } from "react";
import {
  Search,
  Filter,
  X,
  Star,
  Clock,
  User,
  BookOpen,
  Code,
  Palette,
  BarChart3,
  Smartphone,
  Camera,
  Briefcase,
} from "lucide-react";

export default function Courses() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [selectedLevel, setSelectedLevel] = useState("all");
  const [selectedDuration, setSelectedDuration] = useState("all");
  const [showFilters, setShowFilters] = useState(false);

  // Cours avec vraies images et design cohérent
  const courses = [
    {
      id: 1,
      title: "Développeur Web Full Stack",
      description:
        "Maîtrisez JavaScript, React, Node.js et créez des applications web modernes.",
      category: "Développement Web",
      level: "Intermédiaire",
      duration: "6 mois",
      students: 1234,
      rating: 4.9,
      image:
        "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
    {
      id: 2,
      title: "UX/UI Designer",
      description:
        "Concevez des expériences utilisateur exceptionnelles avec Figma et les méthodes UX.",
      category: "Design",
      level: "Débutant",
      duration: "4 mois",
      students: 856,
      rating: 4.8,
      image:
        "https://images.unsplash.com/photo-1611224923853-80b023f02d71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
    {
      id: 3,
      title: "Data Scientist",
      description:
        "Analysez et visualisez les données avec Python, SQL et les outils de Machine Learning.",
      category: "Data Science",
      level: "Avancé",
      duration: "8 mois",
      students: 967,
      rating: 4.7,
      image:
        "https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
    {
      id: 4,
      title: "Marketing Digital",
      description:
        "Maîtrisez les stratégies digitales, SEO, réseaux sociaux et analytics.",
      category: "Marketing",
      level: "Intermédiaire",
      duration: "3 mois",
      students: 743,
      rating: 4.6,
      image:
        "https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
    {
      id: 5,
      title: "Développement Mobile React Native",
      description:
        "Créez des applications mobiles multiplateformes avec React Native.",
      category: "Développement Mobile",
      level: "Intermédiaire",
      duration: "5 mois",
      students: 532,
      rating: 4.8,
      image:
        "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
    {
      id: 6,
      title: "Gestion de Projet Agile",
      description:
        "Apprenez les méthodologies agiles et devenez un chef de projet efficace.",
      category: "Management",
      level: "Débutant",
      duration: "2 mois",
      students: 891,
      rating: 4.5,
      image:
        "https://images.unsplash.com/photo-1552664730-d307ca884978?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
    },
  ];

  const categories = [
    { name: "all", label: "Toutes les catégories", icon: BookOpen },
    { name: "Développement Web", label: "Développement Web", icon: Code },
    { name: "Design", label: "Design UX/UI", icon: Palette },
    { name: "Data Science", label: "Data Science", icon: BarChart3 },
    { name: "Développement Mobile", label: "Mobile", icon: Smartphone },
    { name: "Marketing", label: "Marketing", icon: Camera },
    { name: "Management", label: "Management", icon: Briefcase },
  ];

  const levels = ["all", "Débutant", "Intermédiaire", "Avancé"];
  const durations = ["all", "1-3 mois", "3-6 mois", "6+ mois"];

  const filteredCourses = courses.filter((course) => {
    const matchesSearch =
      course.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      course.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory =
      selectedCategory === "all" || course.category === selectedCategory;
    const matchesLevel =
      selectedLevel === "all" || course.level === selectedLevel;

    let matchesDuration = true;
    if (selectedDuration !== "all") {
      const duration = parseInt(course.duration);
      if (selectedDuration === "1-3 mois") matchesDuration = duration <= 3;
      else if (selectedDuration === "3-6 mois")
        matchesDuration = duration > 3 && duration <= 6;
      else if (selectedDuration === "6+ mois") matchesDuration = duration > 6;
    }

    return matchesSearch && matchesCategory && matchesLevel && matchesDuration;
  });

  const clearFilters = () => {
    setSearchQuery("");
    setSelectedCategory("all");
    setSelectedLevel("all");
    setSelectedDuration("all");
  };

  const hasActiveFilters =
    searchQuery !== "" ||
    selectedCategory !== "all" ||
    selectedLevel !== "all" ||
    selectedDuration !== "all";

  return (
    <div className="min-h-screen bg-white">
      {/* Hero Section avec recherche */}
      <section className="bg-gradient-to-r from-neutral via-white to-neutral py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl lg:text-5xl font-bold text-textPrimary mb-6">
              Explorez Nos <span className="text-primary">Formations</span>
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
              Découvrez des cours conçus par des experts pour faire grandir vos
              compétences et accélérer votre carrière.
            </p>

            {/* Barre de recherche améliorée */}
            <div className="max-w-2xl mx-auto relative mb-8">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Rechercher un cours, une compétence..."
                className="w-full pl-12 pr-4 py-4 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary focus:border-primary transition-all text-lg shadow-sm"
              />
            </div>

            {/* Tags catégories rapides */}
            <div className="flex flex-wrap justify-center gap-3">
              {categories.slice(1, 6).map((category) => {
                const IconComponent = category.icon;
                return (
                  <button
                    key={category.name}
                    onClick={() => setSelectedCategory(category.name)}
                    className={`flex items-center space-x-2 px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                      selectedCategory === category.name
                        ? "bg-primary text-white"
                        : "bg-white text-gray-700 hover:bg-primary hover:text-white border border-gray-200"
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    <span>{category.label}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Filters - Design amélioré */}
          <div className="hidden lg:block w-72 flex-shrink-0">
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 sticky top-24">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-lg font-semibold text-textPrimary flex items-center space-x-2">
                  <Filter className="w-5 h-5" />
                  <span>Filtres</span>
                </h2>
                {hasActiveFilters && (
                  <button
                    onClick={clearFilters}
                    className="text-sm text-primary hover:text-primary/80 transition-colors font-medium"
                  >
                    Tout effacer
                  </button>
                )}
              </div>

              {/* Filtres par catégorie avec icônes */}
              <div className="mb-6">
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Catégorie
                </h3>
                <div className="space-y-2">
                  {categories.map((category) => {
                    const IconComponent = category.icon;
                    return (
                      <label
                        key={category.name}
                        className="flex items-center group cursor-pointer"
                      >
                        <input
                          type="radio"
                          name="category"
                          value={category.name}
                          checked={selectedCategory === category.name}
                          onChange={(e) => setSelectedCategory(e.target.value)}
                          className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                        />
                        <div className="ml-3 flex items-center space-x-2 group-hover:text-primary transition-colors">
                          <IconComponent className="w-4 h-4" />
                          <span className="text-sm">{category.label}</span>
                        </div>
                      </label>
                    );
                  })}
                </div>
              </div>

              {/* Filtre par niveau */}
              <div className="mb-6">
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Niveau
                </h3>
                <div className="space-y-2">
                  {levels.map((level) => (
                    <label
                      key={level}
                      className="flex items-center group cursor-pointer"
                    >
                      <input
                        type="radio"
                        name="level"
                        value={level}
                        checked={selectedLevel === level}
                        onChange={(e) => setSelectedLevel(e.target.value)}
                        className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                      />
                      <span className="ml-3 text-sm group-hover:text-primary transition-colors">
                        {level === "all" ? "Tous les niveaux" : level}
                      </span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Filtre par durée */}
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Durée
                </h3>
                <div className="space-y-2">
                  {durations.map((duration) => (
                    <label
                      key={duration}
                      className="flex items-center group cursor-pointer"
                    >
                      <input
                        type="radio"
                        name="duration"
                        value={duration}
                        checked={selectedDuration === duration}
                        onChange={(e) => setSelectedDuration(e.target.value)}
                        className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                      />
                      <span className="ml-3 text-sm group-hover:text-primary transition-colors">
                        {duration === "all" ? "Toutes durées" : duration}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Mobile Filter Button */}
          <div className="lg:hidden flex items-center justify-between">
            <button
              onClick={() => setShowFilters(true)}
              className="flex items-center space-x-2 bg-white px-4 py-3 rounded-xl border border-gray-200 hover:bg-gray-50 transition-colors shadow-sm"
            >
              <Filter className="w-4 h-4" />
              <span>Filtres</span>
              {hasActiveFilters && (
                <span className="w-2 h-2 bg-primary rounded-full"></span>
              )}
            </button>
          </div>

          {/* Contenu principal */}
          <div className="flex-1">
            {/* Info résultats et filtres actifs */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 gap-4">
              <p className="text-gray-600 font-medium">
                <span className="text-textPrimary font-bold">
                  {filteredCourses.length}
                </span>{" "}
                cours trouvé{filteredCourses.length > 1 ? "s" : ""}
              </p>

              {hasActiveFilters && (
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-sm text-gray-500 font-medium">
                    Filtres actifs:
                  </span>
                  {searchQuery && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary border border-primary/20">
                      "{searchQuery}"
                      <button
                        onClick={() => setSearchQuery("")}
                        className="ml-1 hover:text-primary/80"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </span>
                  )}
                  {selectedCategory !== "all" && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-accent/10 text-accent border border-accent/20">
                      {selectedCategory}
                      <button
                        onClick={() => setSelectedCategory("all")}
                        className="ml-1 hover:text-accent/80"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </span>
                  )}
                  {selectedLevel !== "all" && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-success/10 text-success border border-success/20">
                      {selectedLevel}
                      <button
                        onClick={() => setSelectedLevel("all")}
                        className="ml-1 hover:text-success/80"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </span>
                  )}
                </div>
              )}
            </div>

            {/* Grille de cours - Même design que l'accueil */}
            {filteredCourses.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
                {filteredCourses.map((course) => (
                  <a
                    key={course.id}
                    href={`/course/${course.id}`}
                    className="group"
                  >
                    <div
                      key={course.id}
                      className="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow overflow-hidden"
                    >
                      <img
                        src={course.image}
                        alt={course.title}
                        className="w-full h-48 object-cover"
                      />
                      <div className="p-6">
                        <div className="flex items-center justify-between mb-3">
                          <span className="bg-primary/10 text-primary px-3 py-1 rounded-full text-xs font-medium">
                            {course.category}
                          </span>
                          <div className="flex items-center space-x-1">
                            <Star className="w-4 h-4 text-accent fill-current" />
                            <span className="text-sm text-gray-600 font-medium">
                              {course.rating}
                            </span>
                          </div>
                        </div>

                        <h3 className="font-bold text-lg text-textPrimary mb-2 line-clamp-2">
                          {course.title}
                        </h3>

                        <p className="text-gray-600 text-sm mb-4 line-clamp-2">
                          {course.description}
                        </p>

                        <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                          <div className="flex items-center space-x-4">
                            <div className="flex items-center space-x-1">
                              <Clock className="w-4 h-4" />
                              <span>{course.duration}</span>
                            </div>
                            <div className="flex items-center space-x-1">
                              <User className="w-4 h-4" />
                              <span>{course.students.toLocaleString()}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </a>
                ))}
              </div>
            ) : (
              <div className="text-center py-16">
                <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                  <Search className="w-12 h-12 text-gray-400" />
                </div>
                <h3 className="text-2xl font-bold text-textPrimary mb-2">
                  Aucun cours trouvé
                </h3>
                <p className="text-gray-600 mb-8 max-w-md mx-auto">
                  Essayez de modifier vos critères de recherche ou explorez
                  toutes nos catégories.
                </p>
                <button
                  onClick={clearFilters}
                  className="bg-primary text-white px-8 py-3 rounded-xl hover:bg-primary/90 transition-colors font-semibold"
                >
                  Effacer tous les filtres
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modal filtres mobile */}
      {showFilters && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div
            className="fixed inset-0 bg-black bg-opacity-50"
            onClick={() => setShowFilters(false)}
          />
          <div className="fixed inset-y-0 right-0 max-w-sm w-full bg-white shadow-xl">
            <div className="flex items-center justify-between p-6 border-b border-gray-200">
              <h2 className="text-lg font-semibold text-textPrimary">
                Filtres
              </h2>
              <button
                onClick={() => setShowFilters(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="p-6 space-y-6 overflow-y-auto h-full pb-24">
              {/* Filtres mobiles identiques */}
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Catégorie
                </h3>
                <div className="space-y-3">
                  {categories.map((category) => {
                    const IconComponent = category.icon;
                    return (
                      <label key={category.name} className="flex items-center">
                        <input
                          type="radio"
                          name="category-mobile"
                          value={category.name}
                          checked={selectedCategory === category.name}
                          onChange={(e) => setSelectedCategory(e.target.value)}
                          className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                        />
                        <div className="ml-3 flex items-center space-x-2">
                          <IconComponent className="w-4 h-4" />
                          <span className="text-sm">{category.label}</span>
                        </div>
                      </label>
                    );
                  })}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Niveau
                </h3>
                <div className="space-y-3">
                  {levels.map((level) => (
                    <label key={level} className="flex items-center">
                      <input
                        type="radio"
                        name="level-mobile"
                        value={level}
                        checked={selectedLevel === level}
                        onChange={(e) => setSelectedLevel(e.target.value)}
                        className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                      />
                      <span className="ml-3 text-sm">
                        {level === "all" ? "Tous les niveaux" : level}
                      </span>
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">
                  Durée
                </h3>
                <div className="space-y-3">
                  {durations.map((duration) => (
                    <label key={duration} className="flex items-center">
                      <input
                        type="radio"
                        name="duration-mobile"
                        value={duration}
                        checked={selectedDuration === duration}
                        onChange={(e) => setSelectedDuration(e.target.value)}
                        className="w-4 h-4 text-primary focus:ring-primary border-gray-300"
                      />
                      <span className="ml-3 text-sm">
                        {duration === "all" ? "Toutes durées" : duration}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
            </div>

            <div className="absolute bottom-0 left-0 right-0 p-6 border-t border-gray-200 bg-white">
              <div className="flex space-x-3">
                <button
                  onClick={clearFilters}
                  className="flex-1 px-4 py-3 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors font-medium"
                >
                  Effacer
                </button>
                <button
                  onClick={() => setShowFilters(false)}
                  className="flex-1 px-4 py-3 bg-primary text-white rounded-xl hover:bg-primary/90 transition-colors font-medium"
                >
                  Appliquer
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
