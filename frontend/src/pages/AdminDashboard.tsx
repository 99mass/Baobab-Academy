import { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  Plus, 
  BookOpen, 
  Users, 
  BarChart3, 
  Edit, 
  Eye,
  TrendingUp,
  Award,
  Search,
  Filter,
  MoreVertical,
  Calendar,
  Clock,
  Star,
  Activity
} from 'lucide-react';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [searchQuery, setSearchQuery] = useState('');

  // Données mockées avec plus de réalisme
  const courses = [
    {
      id: 1,
      title: "Développeur Web Full Stack",
      category: "Développement Web",
      level: "Intermédiaire",
      duration: "6 mois",
      students: 1234,
      rating: 4.9,
      status: "Publié",
      image: "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
      lastUpdate: "Il y a 2 jours",
      revenue: "15,600€"
    },
    {
      id: 2,
      title: "UX/UI Designer Moderne",
      category: "Design",
      level: "Débutant",
      duration: "4 mois",
      students: 856,
      rating: 4.8,
      status: "Publié",
      image: "https://images.unsplash.com/photo-1611224923853-80b023f02d71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
      lastUpdate: "Il y a 1 semaine",
      revenue: "8,400€"
    },
    {
      id: 3,
      title: "Data Science & IA",
      category: "Data Science",
      level: "Avancé",
      duration: "8 mois",
      students: 567,
      rating: 4.7,
      status: "Brouillon",
      image: "https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
      lastUpdate: "Il y a 3 jours",
      revenue: "12,300€"
    }
  ];

  const stats = {
    totalCourses: courses.length,
    totalStudents: 2657,
    totalCompletions: 1456,
    averageRating: 4.8,
    monthlyRevenue: "36,300€",
    activeUsers: 1842
  };

  const filteredCourses = courses.filter(course =>
    course.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    course.category.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const recentActivities = [
    { 
      type: 'enrollment', 
      message: 'Nouvel étudiant inscrit au cours "Développeur Web Full Stack"', 
      time: 'Il y a 5 min',
      user: 'Aminata Diop'
    },
    { 
      type: 'completion', 
      message: 'Cours "UX/UI Designer" terminé avec succès', 
      time: 'Il y a 1h',
      user: 'Moussa Ba'
    },
    { 
      type: 'review', 
      message: 'Nouvelle évaluation 5★ pour "Data Science & IA"', 
      time: 'Il y a 2h',
      user: 'Fatou Sall'
    },
    { 
      type: 'course', 
      message: 'Nouveau chapitre ajouté à "Marketing Digital"', 
      time: 'Il y a 4h',
      user: 'Admin'
    }
  ];

  return (
    <div className="min-h-screen bg-neutral">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header moderne */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-textPrimary mb-2">
                Tableau de bord
              </h1>
              <p className="text-gray-600 flex items-center space-x-2">
                <Calendar className="w-4 h-4" />
                <span>Aujourd'hui, {new Date().toLocaleDateString('fr-FR', { 
                  weekday: 'long', 
                  year: 'numeric', 
                  month: 'long', 
                  day: 'numeric' 
                })}</span>
              </p>
            </div>
            <div className="flex items-center space-x-3">
              <div className="bg-white px-4 py-2 rounded-lg border border-gray-200 text-sm text-gray-600">
                <span className="w-2 h-2 bg-success rounded-full inline-block mr-2"></span>
                Plateforme active
              </div>
              <Link
                to="/admin/course/new"
                className="flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-xl hover:bg-primary/90 transition-all duration-300 hover:scale-105 font-semibold shadow-md"
              >
                <Plus className="w-5 h-5" />
                <span>Nouveau cours</span>
              </Link>
            </div>
          </div>
        </div>

        {/* Navigation moderne */}
        <div className="flex space-x-2 mb-8 bg-white rounded-xl p-2 shadow-sm border border-gray-200 w-fit">
          {[
            { id: 'dashboard', label: 'Vue d\'ensemble', icon: BarChart3 },
            { id: 'courses', label: 'Cours', icon: BookOpen },
            { id: 'users', label: 'Étudiants', icon: Users }
          ].map((tab) => {
            const IconComponent = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center space-x-2 px-6 py-3 rounded-lg font-medium transition-all duration-200 ${
                  activeTab === tab.id
                    ? 'bg-primary text-white shadow-md'
                    : 'text-gray-600 hover:text-textPrimary hover:bg-gray-50'
                }`}
              >
                <IconComponent className="w-4 h-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* Dashboard Content */}
        {activeTab === 'dashboard' && (
          <div className="space-y-8">
            {/* Stats Cards améliorées */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
                    <BookOpen className="w-6 h-6 text-primary" />
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-textPrimary">{stats.totalCourses}</p>
                    <p className="text-sm text-gray-600">Cours créés</p>
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-1 text-sm">
                    <TrendingUp className="w-4 h-4 text-success" />
                    <span className="text-success font-semibold">+2</span>
                    <span className="text-gray-600">ce mois</span>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-accent/10 rounded-xl flex items-center justify-center">
                    <Users className="w-6 h-6 text-accent" />
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-textPrimary">{stats.totalStudents.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Étudiants inscrits</p>
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-1 text-sm">
                    <TrendingUp className="w-4 h-4 text-success" />
                    <span className="text-success font-semibold">+156</span>
                    <span className="text-gray-600">ce mois</span>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-success/10 rounded-xl flex items-center justify-center">
                    <Award className="w-6 h-6 text-success" />
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-textPrimary">{stats.totalCompletions.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Cours complétés</p>
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-1 text-sm">
                    <TrendingUp className="w-4 h-4 text-success" />
                    <span className="text-success font-semibold">+89</span>
                    <span className="text-gray-600">ce mois</span>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-yellow-100 rounded-xl flex items-center justify-center">
                    <Star className="w-6 h-6 text-yellow-600" />
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-textPrimary">{stats.averageRating}/5</p>
                    <p className="text-sm text-gray-600">Note moyenne</p>
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-1 text-sm">
                    <TrendingUp className="w-4 h-4 text-success" />
                    <span className="text-success font-semibold">+0.2</span>
                    <span className="text-gray-600">ce mois</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Section avec 2 colonnes */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Activité récente */}
              <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-200">
                <div className="p-6 border-b border-gray-200">
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-semibold text-textPrimary flex items-center space-x-2">
                      <Activity className="w-5 h-5" />
                      <span>Activité récente</span>
                    </h3>
                    <button className="text-sm text-primary hover:text-primary/80 font-medium">
                      Voir tout
                    </button>
                  </div>
                </div>
                <div className="p-6">
                  <div className="space-y-4">
                    {recentActivities.map((activity, index) => (
                      <div key={index} className="flex items-start space-x-4 p-4 rounded-lg hover:bg-gray-50 transition-colors">
                        <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                          activity.type === 'enrollment' ? 'bg-primary/10' :
                          activity.type === 'completion' ? 'bg-success/10' :
                          activity.type === 'review' ? 'bg-yellow-100' :
                          'bg-accent/10'
                        }`}>
                          {activity.type === 'enrollment' && <Users className="w-5 h-5 text-primary" />}
                          {activity.type === 'completion' && <Award className="w-5 h-5 text-success" />}
                          {activity.type === 'review' && <Star className="w-5 h-5 text-yellow-600" />}
                          {activity.type === 'course' && <BookOpen className="w-5 h-5 text-accent" />}
                        </div>
                        <div className="flex-1">
                          <p className="font-medium text-textPrimary text-sm">{activity.message}</p>
                          <div className="flex items-center space-x-2 mt-1">
                            <span className="text-xs text-gray-500">{activity.user}</span>
                            <span className="text-xs text-gray-400">•</span>
                            <span className="text-xs text-gray-500">{activity.time}</span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Quick stats */}
              <div className="space-y-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                  <h4 className="font-semibold text-textPrimary mb-4">Performance mensuelle</h4>
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-gray-600">Nouveaux étudiants</span>
                      <span className="font-semibold text-textPrimary">+156</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-gray-600">Taux de completion</span>
                      <span className="font-semibold text-success">78%</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-gray-600">Satisfaction</span>
                      <span className="font-semibold text-yellow-600">4.8★</span>
                    </div>
                  </div>
                </div>

                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                  <h4 className="font-semibold text-textPrimary mb-4">Cours populaires</h4>
                  <div className="space-y-3">
                    {courses.slice(0, 3).map((course, index) => (
                      <div key={course.id} className="flex items-center space-x-3">
                        <div className="w-8 h-8 bg-primary/10 rounded-lg flex items-center justify-center">
                          <span className="text-sm font-bold text-primary">#{index + 1}</span>
                        </div>
                        <div className="flex-1">
                          <p className="text-sm font-medium text-textPrimary truncate">{course.title}</p>
                          <p className="text-xs text-gray-500">{course.students} étudiants</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Course Management amélioré */}
        {activeTab === 'courses' && (
          <div className="space-y-6">
            {/* Header avec recherche et filtres */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                <div className="flex-1 flex items-center space-x-4">
                  <div className="relative flex-1 max-w-md">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      type="text"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder="Rechercher un cours..."
                      className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
                    />
                  </div>
                  <button className="flex items-center space-x-2 px-4 py-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                    <Filter className="w-4 h-4" />
                    <span>Filtres</span>
                  </button>
                </div>
                <Link
                  to="/admin/course/new"
                  className="flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-all duration-300 hover:scale-105 font-semibold shadow-md"
                >
                  <Plus className="w-5 h-5" />
                  <span>Nouveau cours</span>
                </Link>
              </div>
            </div>

            {/* Courses Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
              {filteredCourses.map((course) => (
                <div key={course.id} className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-all duration-300 group">
                  <div className="relative">
                    <img
                      src={course.image}
                      alt={course.title}
                      className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <div className="absolute top-4 left-4">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        course.status === 'Publié' 
                          ? 'bg-success/20 text-success' 
                          : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {course.status}
                      </span>
                    </div>
                    <div className="absolute top-4 right-4">
                      <button className="w-8 h-8 bg-white/90 rounded-full flex items-center justify-center hover:bg-white transition-colors">
                        <MoreVertical className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                  
                  <div className="p-6">
                    <div className="flex items-center space-x-2 mb-3">
                      <span className="bg-primary/10 text-primary px-2 py-1 rounded-md text-xs font-medium">
                        {course.category}
                      </span>
                      <span className={`px-2 py-1 rounded-md text-xs font-medium ${
                        course.level === 'Débutant' ? 'bg-green-100 text-green-800' :
                        course.level === 'Intermédiaire' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {course.level}
                      </span>
                    </div>
                    
                    <h3 className="font-bold text-lg text-textPrimary mb-2 line-clamp-2 group-hover:text-primary transition-colors">
                      {course.title}
                    </h3>
                    
                    <div className="grid grid-cols-2 gap-4 mb-4 text-sm text-gray-600">
                      <div className="flex items-center space-x-2">
                        <Users className="w-4 h-4" />
                        <span>{course.students.toLocaleString()}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Clock className="w-4 h-4" />
                        <span>{course.duration}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Star className="w-4 h-4 text-yellow-500 fill-current" />
                        <span>{course.rating}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <BarChart3 className="w-4 h-4" />
                        <span className="font-semibold text-success">{course.revenue}</span>
                      </div>
                    </div>
                    
                    <p className="text-xs text-gray-500 mb-4">Mis à jour {course.lastUpdate}</p>
                    
                    <div className="flex items-center space-x-2">
                      <Link
                        to={`/course/${course.id}`}
                        className="flex-1 flex items-center justify-center space-x-2 px-3 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-sm"
                      >
                        <Eye className="w-4 h-4" />
                        <span>Voir</span>
                      </Link>
                      <Link
                        to={`/admin/course/edit/${course.id}`}
                        className="flex-1 flex items-center justify-center space-x-2 px-3 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors text-sm"
                      >
                        <Edit className="w-4 h-4" />
                        <span>Modifier</span>
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {filteredCourses.length === 0 && (
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
                <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-textPrimary mb-2">Aucun cours trouvé</h3>
                <p className="text-gray-600 mb-6">Commencez par créer votre premier cours pour vos étudiants.</p>
                <Link
                  to="/admin/course/new"
                  className="inline-flex items-center space-x-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-colors font-medium"
                >
                  <Plus className="w-5 h-5" />
                  <span>Créer un cours</span>
                </Link>
              </div>
            )}
          </div>
        )}

        {/* Users Management */}
        {activeTab === 'users' && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <div className="text-center py-12">
              <div className="w-20 h-20 bg-accent/10 rounded-full flex items-center justify-center mx-auto mb-6">
                <Users className="w-10 h-10 text-accent" />
              </div>
              <h3 className="text-xl font-semibold text-textPrimary mb-2">Gestion des étudiants</h3>
              <p className="text-gray-600 max-w-md mx-auto mb-6">
                Consultez les profils des étudiants, suivez leur progression et gérez leurs accès aux cours.
              </p>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-2xl mx-auto">
                <div className="text-center">
                  <div className="text-2xl font-bold text-textPrimary">{stats.totalStudents.toLocaleString()}</div>
                  <div className="text-sm text-gray-600">Étudiants inscrits</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-textPrimary">{stats.activeUsers.toLocaleString()}</div>
                  <div className="text-sm text-gray-600">Utilisateurs actifs</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-textPrimary">78%</div>
                  <div className="text-sm text-gray-600">Taux d'engagement</div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}